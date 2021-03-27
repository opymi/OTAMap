package com.opymi.otamap.util;

import com.opymi.otamap.exception.CreateInstanceException;
import com.opymi.otamap.exception.AccessPropertyException;
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
     * Shallow automated
     * Build the target object from origin object just using, for the inner complex objects,
     * OTMappers {@link OTMapper} defined by user
     *
     * @param origin object
     * @param target object
     * @return buided target object
     */
    public final TARGET build(ORIGIN origin, TARGET target) {
        return build(origin, target, false);
    }

    /**
     * Deep automated
     * Build the target object from origin object using, for the inner complex objects,
     * default {@link OTMapper} if the user didn't define it and {@param deepAutomatedBuild} is true
     *
     * @param origin origin object
     * @param target target object
     * @param deepAutomatedBuild mapping mode
     * @return buided target object
     *
     * @throws AccessPropertyException if it isn't possible to read origin's property or write target's property
     */
    public final TARGET build(ORIGIN origin, TARGET target, boolean deepAutomatedBuild) {
        mapper.getMappedProperties().forEach((originProperty, targetProperty) -> {
            try {
                writeProperty(origin, originProperty, target, targetProperty, deepAutomatedBuild);
            } catch (IllegalAccessException | InvocationTargetException cause) {
                String message = "CANNOT READ ORIGIN'S PROPERTY " + originProperty.getName()
                        + " OR CANNOT WRITE TARGET'S PROPERTY " + targetProperty.getName();
                throw new AccessPropertyException(message, cause);
            }
        });

        OTCustomMapperOperation<ORIGIN, TARGET> OTCustomMapperOperation = mapper.getCustomMapper();
        if (OTCustomMapperOperation != null) {
            OTCustomMapperOperation.customMap(origin, target);
        }

        return target;
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
                try {
                    OTABuilder builder = instance(originPropertyType, targetPropertyType);
                    value = builder.build(value, targetPropertyType.getDeclaredConstructor().newInstance(), true);
                    targetProperty.getWriteMethod().invoke(target, value);
                } catch (InstantiationException | NoSuchMethodException | InvocationTargetException cause) {
                    throw new CreateInstanceException("CANNOT CREATE INSTANCE OF " + targetPropertyType, cause);
                }
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
}
