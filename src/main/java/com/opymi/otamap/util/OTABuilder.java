package com.opymi.otamap.util;

import com.opymi.otamap.exception.AccessPropertyException;
import com.opymi.otamap.exception.CreateInstanceException;
import com.opymi.otamap.internal.OTMapperFactory;
import com.opymi.otamap.internal.OTMapperRepository;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;

/**
 * Class that builds a {@param <TARGET>} object from an {@param <ORIGIN>} object
 *
 * @author Antonino Verde
 * @since 1.0
 */
public class OTABuilder<ORIGIN, TARGET> {

    private static final Map<Class<?>, Class<?>> SIMPLE_WRAPPER_TYPES = Map.of(
            Boolean.class, boolean.class,
            Integer.class, int.class,
            Double.class, double.class,
            Float.class, float.class,
            Long.class, long.class,
            Short.class, short.class,
            Character.class, char.class,
            Byte.class, byte.class
    );

    private final OTMapper<ORIGIN, TARGET> mapper;

    private OTABuilder(OTMapper<ORIGIN, TARGET> mapper) {
        this.mapper = mapper;
    }

    private OTABuilder(Class<ORIGIN> origin, Class<TARGET> target) {
        this(OTMapperFactory.mapperForClasses(origin, target));
    }

    /**
     * Create an {@link OTABuilder} instance from origin's and target's mapper
     *
     * @param mapper origin's and target's mapper
     * @param <ORIGIN> origin object
     * @param <TARGET> target object
     * @return {@link OTABuilder} instance
     */
    public static <ORIGIN, TARGET> OTABuilder<ORIGIN, TARGET> instance(OTMapper<ORIGIN, TARGET> mapper) {
        return new OTABuilder<>(mapper);
    }

    /**
     * Create an {@link OTABuilder} instance from origin's type and target's type
     *
     * @param <ORIGIN> origin type
     * @param <TARGET> target type
     * @return {@link OTABuilder} instance
     */
    public static <ORIGIN, TARGET> OTABuilder<ORIGIN, TARGET> instance(Class<ORIGIN> origin, Class<TARGET> target) {
        return new OTABuilder<>(origin, target);
    }

    /**
     * Build the target's object from origin's object.
     * If target is null try to create an instance of it.
     * If the inner complex objects have specific mapper {@link OTMapper} defined by user,
     * then the current method uses it, otherwise, if {@param deepAutomatedBuild} is true,
     * the current method uses the default mapper for current types.
     *
     * @param origin origin object
     * @param target target object
     * @param deepAutomatedBuild mapping mode
     * @return builded target object
     *
     * @throws AccessPropertyException if it isn't possible to read origin's property or write target's property
     *
     * @throws CreateInstanceException if this method is trying to create an instance of target but
     * default constructor doesn't exists for the type
     */
    public final TARGET build(ORIGIN origin, TARGET target, boolean deepAutomatedBuild) {
        final TARGET newTarget = target != null ? target : createInstance(mapper.getTargetClass());

        mapper.getMappedProperties().forEach((originProperty, targetProperty) -> {
            try {
                writeProperty(origin, originProperty, newTarget, targetProperty, deepAutomatedBuild);
            } catch (IllegalAccessException | InvocationTargetException cause) {
                String message = "CANNOT READ ORIGIN'S PROPERTY " + originProperty.getName()
                        + " OR CANNOT WRITE TARGET'S PROPERTY " + targetProperty.getName();
                throw new AccessPropertyException(message, cause);
            }
        });

        OTCustomMapperOperation<ORIGIN, TARGET> OTCustomMapperOperation = mapper.getCustomMapper();
        if (OTCustomMapperOperation != null) {
            OTCustomMapperOperation.customMap(origin, newTarget);
        }

        return newTarget;
    }

    /**
     * See {@link OTABuilder#build(ORIGIN, TARGET, boolean)}
     */
    public final TARGET build(ORIGIN origin, TARGET target) {
        return build(origin, target, false);
    }

    /**
     * See {@link OTABuilder#build(ORIGIN, TARGET, boolean)}
     */
    public final TARGET build(ORIGIN origin) {
        return build(origin, null, false);
    }

    /**
     * See {@link OTABuilder#build(ORIGIN, TARGET, boolean)}
     */
    public final TARGET build(ORIGIN origin, boolean deepAutomatedBuild) {
        return build(origin, null, deepAutomatedBuild);
    }


    /**
     * Write origin's property's value to target's property if they are of the same type or if they are attributable
     * to the same primitive type; otherwise, if {@param deepAutomatedMap} is true or if a mapper defined
     * for types exists, try to build the target's property from origin's property through a specific instance
     * of {@link OTABuilder}.
     *
     * @param origin origin object
     * @param originProperty {@link PropertyDescriptor} of origin's property
     * @param target target object
     * @param targetProperty {@link PropertyDescriptor} of target's property
     * @param deepAutomatedBuild mapping mode
     *
     * @throws IllegalAccessException if this method object is enforcing Java language access control and the
     * underlying method is inaccessible
     *
     * @throws InvocationTargetException if the underlying method throws an exception
     *
     * @throws CreateInstanceException if this method is trying to create an instance of target's property but
     * default constructor doesn't exists for the property's type
     */
    @SuppressWarnings("unchecked, rawtypes")
    private void writeProperty(ORIGIN origin, PropertyDescriptor originProperty, TARGET target,
                               PropertyDescriptor targetProperty, boolean deepAutomatedBuild)
            throws IllegalAccessException, InvocationTargetException {

        Object value = originProperty.getReadMethod().invoke(origin);
        if (value != null) {
            Class<?> targetPropertyType = targetProperty.getPropertyType();
            Class<?> originPropertyType = originProperty.getPropertyType();
            if (Objects.equals(originPropertyType, targetPropertyType) || primitivable(originPropertyType, targetPropertyType)) {
                targetProperty.getWriteMethod().invoke(target, value);
            } else if (deepAutomatedBuild || OTMapperRepository.exists(originPropertyType, targetPropertyType)) {
                OTABuilder builder = instance(originPropertyType, targetPropertyType);
                value = builder.build(value, createInstance(targetPropertyType), deepAutomatedBuild);
                targetProperty.getWriteMethod().invoke(target, value);
            }
        }
    }

    /**
     *
     * @param origin origin type
     * @param target target type
     * @return if the origin is primitive and the target is a wrapper for the same primitive or viceversa
     */
    private boolean primitivable(Class<?> origin, Class<?> target) {
        return (origin.isPrimitive() && Objects.equals(origin, SIMPLE_WRAPPER_TYPES.get(target)))
                || (target.isPrimitive() && Objects.equals(target, SIMPLE_WRAPPER_TYPES.get(origin)));
    }

    /**
     * @return an instance of {@param type} class
     */
    private <T> T createInstance(Class<T> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException cause) {
            throw new CreateInstanceException("CANNOT CREATE INSTANCE OF " + type.getName(), cause);
        }
    }

}
