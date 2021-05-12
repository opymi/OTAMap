/*
 * MIT License
 *
 * Copyright (c) 2021. Antonino Verde
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.opymi.otamap.util;

import com.opymi.otamap.exception.AccessPropertyException;
import com.opymi.otamap.exception.CreateInstanceException;
import com.opymi.otamap.exception.OTException;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Class that builds a {@param <TARGET>} object from an {@param <ORIGIN>} object
 *
 * @author Antonino Verde
 * @since 1.0
 */
public class OTABuilder<ORIGIN, TARGET> {
    private static final Map<Class<?>, Class<?>> SIMPLE_WRAPPER_TYPES;
    static {
        SIMPLE_WRAPPER_TYPES = new HashMap<>();
        SIMPLE_WRAPPER_TYPES.put(Boolean.class, boolean.class);
        SIMPLE_WRAPPER_TYPES.put(Integer.class, int.class);
        SIMPLE_WRAPPER_TYPES.put(Double.class, double.class);
        SIMPLE_WRAPPER_TYPES.put(Float.class, float.class);
        SIMPLE_WRAPPER_TYPES.put(Long.class, long.class);
        SIMPLE_WRAPPER_TYPES.put(Short.class, short.class);
        SIMPLE_WRAPPER_TYPES.put(Character.class, char.class);
        SIMPLE_WRAPPER_TYPES.put(Byte.class, byte.class);
    }

    private static final Logger logger = Logger.getLogger(OTABuilder.class.getSimpleName());
    private final OTMapper<ORIGIN, TARGET> mapper;
    private final OTMapperRepository repository;
    private final OTMapperFactory factory;
    private final String BASE_MESSAGE;

    private OTABuilder(OTMapperRepository repository, OTMapper<ORIGIN, TARGET> mapper) {
        if(mapper == null) {
            throw new OTException("NULL MANDATORY MAPPER");
        }
        if (repository != null && !repository.exists(mapper.getOriginClass(), mapper.getTargetClass())) {
            repository.store(mapper);
        }
        this.repository = repository;
        this.factory = new OTMapperFactory(repository);
        this.mapper = mapper;
        this.BASE_MESSAGE = getBaseMessage(mapper.getOriginClass(), mapper.getTargetClass());
    }

    private OTABuilder(OTMapperRepository repository, Class<ORIGIN> origin, Class<TARGET> target) {
        this.repository = repository;
        this.factory = new OTMapperFactory(repository);
        this.mapper = factory.mapperForClasses(origin, target);
        this.BASE_MESSAGE = getBaseMessage(origin, target);
    }

    /**
     * Create an {@link OTABuilder} instance from origin's and target's mapper
     *
     * @param mapper origin's and target's mapper
     * @param <ORIGIN> origin object
     * @param <TARGET> target object
     * @return {@link OTABuilder} instance
     */
    public static <ORIGIN, TARGET> OTABuilder<ORIGIN, TARGET> instance(OTMapperRepository repository, OTMapper<ORIGIN, TARGET> mapper) {
        return new OTABuilder<>(repository, mapper);
    }

    /**
     * @see #instance(OTMapperRepository, OTMapper)
     */
    public static <ORIGIN, TARGET> OTABuilder<ORIGIN, TARGET> instance(OTMapper<ORIGIN, TARGET> mapper) {
        return instance(null, mapper);
    }

    /**
     * Create an {@link OTABuilder} instance from origin's type and target's type
     *
     * @param <ORIGIN> origin type
     * @param <TARGET> target type
     * @return {@link OTABuilder} instance
     */
    public static <ORIGIN, TARGET> OTABuilder<ORIGIN, TARGET> instance(OTMapperRepository repository, Class<ORIGIN> origin, Class<TARGET> target) {
        return new OTABuilder<>(repository, origin, target);
    }

    /**
     * @see #instance(OTMapperRepository, Class, Class)
     */
    public static <ORIGIN, TARGET> OTABuilder<ORIGIN, TARGET> instance(Class<ORIGIN> origin, Class<TARGET> target) {
        return instance(null, origin, target);
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
    @SuppressWarnings("unchecked")
    public final TARGET build(ORIGIN origin, TARGET target, boolean deepAutomatedBuild) {
        if (origin == null) {
            return null;
        }

        final TARGET newTarget = target != null ? target : createInstance(mapper.getTargetClass(), BASE_MESSAGE);

        Class<?> originClass = origin.getClass();
        Class<?> targetClass = newTarget.getClass();
        if (Objects.equals(originClass, targetClass) || primitivable(originClass, targetClass)) {
            return (TARGET) origin;
        }

        verifyAndGetMapping(mapper).forEach((originProperty, targetProperty) -> {
            try {
                writeProperty(origin, originProperty, newTarget, targetProperty, deepAutomatedBuild);
            } catch (IllegalAccessException | InvocationTargetException cause) {
                String message = "CANNOT READ ORIGIN'S PROPERTY " + originProperty.getName() + " OR CANNOT WRITE TARGET'S PROPERTY " + targetProperty.getName();
                throw new AccessPropertyException(String.format(BASE_MESSAGE, message), cause);
            }
        });

        OTCustomMapperOperation<ORIGIN, TARGET> OTCustomMapperOperation = mapper.getCustomMapper();
        if (OTCustomMapperOperation != null) {
            OTCustomMapperOperation.customMap(origin, newTarget);
        }

        return newTarget;
    }

    /**
     * @see OTABuilder#build(ORIGIN, TARGET, boolean)
     */
    public final TARGET build(ORIGIN origin, TARGET target) {
        return build(origin, target, false);
    }

    /**
     * @see OTABuilder#build(ORIGIN, TARGET, boolean)
     */
    public final TARGET build(ORIGIN origin) {
        return build(origin, null, false);
    }

    /**
     * @see OTABuilder#build(ORIGIN, TARGET, boolean)
     */
    public final TARGET build(ORIGIN origin, boolean deepAutomatedBuild) {
        return build(origin, null, deepAutomatedBuild);
    }

    //TODO write documentation
    private <O, T> Map<PropertyDescriptor, PropertyDescriptor> verifyAndGetMapping(OTMapper<O, T> mapper) {
        final String message = "VERIFY MAPPING " + getBaseMessage(mapper.getOriginClass(), mapper.getTargetClass());
        Map<PropertyDescriptor, PropertyDescriptor> mappedProperties;
        try {
            mappedProperties = mapper.getMappedProperties();
        } catch (Exception cause) {
            String failedMessage = String.format(message, "FAILED! CAUSE: ");
            throw new OTException(failedMessage + cause.getMessage(), cause);
        }
        logger.info(String.format(message, "SUCCESS"));
        mappedProperties.forEach((originProperty, targetProperty) -> {
            Class<?> originPropertyType = originProperty.getPropertyType();
            Class<?> targetPropertyType = targetProperty.getPropertyType();
            if (!Objects.equals(originPropertyType, targetPropertyType) && !primitivable(originPropertyType, targetPropertyType)) {
                verifyAndGetMapping(factory.mapperForClasses(originPropertyType, targetPropertyType));
            }
        });
        return mappedProperties;
    }

    //TODO write documentation
    public final void verifyMapping() {
        verifyAndGetMapping(mapper);
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
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void writeProperty(ORIGIN origin, PropertyDescriptor originProperty, TARGET target, PropertyDescriptor targetProperty, boolean deepAutomatedBuild) throws IllegalAccessException, InvocationTargetException {
        Object value = origin != null ? originProperty.getReadMethod().invoke(origin) : null;
        if (value != null) {
            Class<?> targetPropertyType = targetProperty.getPropertyType();
            Class<?> originPropertyType = originProperty.getPropertyType();
            if (Objects.equals(originPropertyType, targetPropertyType) || primitivable(originPropertyType, targetPropertyType)) {
                targetProperty.getWriteMethod().invoke(target, value);
            } else if (deepAutomatedBuild || (repository != null && repository.exists(originPropertyType, targetPropertyType))) {
                OTABuilder builder = instance(repository, originPropertyType, targetPropertyType);
                value = builder.build(value, createInstance(targetPropertyType, getBaseMessage(originProperty, targetProperty)), deepAutomatedBuild);
                targetProperty.getWriteMethod().invoke(target, value);
            }
            if (targetProperty.getReadMethod() != null) {
                Object targetValue = targetProperty.getReadMethod().invoke(target);
                if (targetValue == null || (primitivable(originPropertyType, targetPropertyType) && !value.equals(targetValue))) {
                    String message = String.format(BASE_MESSAGE, targetProperty.getName() + " WRITE PROPERTY FAILED! EXPECTED VALUE = " + value + " - ACTUAL VALUE = " + targetValue + "! EXCLUDE FIELD AND ADD CUSTOM MAPPING");
                    throw new OTException(message);
                }
            }
        }
    }

    /**
     * @return an instance of {@param type} class
     */
    private <T> T createInstance(Class<T> type, String baseMessage) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException | IllegalArgumentException | ExceptionInInitializerError cause) {
            String message = String.format(baseMessage, "CANNOT CREATE INSTANCE OF " + type.getName());
            throw new CreateInstanceException(message, cause);
        }
    }

    /**
     *
     * @param origin origin type
     * @param target target type
     * @return if the origin is primitive and the target is a wrapper for the same primitive or viceversa
     */
    private boolean primitivable(Class<?> origin, Class<?> target) {
        return (origin.isPrimitive() && Objects.equals(origin, SIMPLE_WRAPPER_TYPES.get(target))) || (target.isPrimitive() && Objects.equals(target, SIMPLE_WRAPPER_TYPES.get(origin)));
    }

    private String getBaseMessage(Class<?> origin, Class<?> target) {
        return origin.getCanonicalName() + " -> " + target.getCanonicalName() + ": %s";
    }

    private String getBaseMessage(PropertyDescriptor originProperty, PropertyDescriptor targetProperty) {
        String propertiesMessage = "PROPERTIES " + originProperty.getName() + " -> " + targetProperty.getName() + " OF TYPES " + getBaseMessage(originProperty.getPropertyType(), targetProperty.getPropertyType()) + ": %s";
        return String.format(BASE_MESSAGE, propertiesMessage);
    }

}