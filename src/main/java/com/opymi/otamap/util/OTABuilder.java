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
import com.opymi.otamap.util.converter.OTConverter;
import com.opymi.otamap.util.mapper.OTCustomMapperOperation;
import com.opymi.otamap.util.mapper.OTMapper;
import com.opymi.otamap.util.mapper.OTMapperFactory;

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
    private final Class<ORIGIN> originType;
    private final Class<TARGET> targetType;
    private final OTRepository repository;
    private final String BASE_MESSAGE;

    private <REPO extends OTRepository, TRANSMUTER extends OTTransmuter<ORIGIN, TARGET>> OTABuilder(REPO repository, TRANSMUTER transmuter) {
        if(transmuter == null) {
            throw new OTException("NULL MANDATORY MAPPER");
        }
        this.originType = transmuter.getOriginType();
        this.targetType = transmuter.getTargetType();
        this.repository = repository != null ? repository : new OTRepository();
        if (!this.repository.exists(originType, targetType)) {
            this.repository.store(transmuter);
        }
        this.BASE_MESSAGE = getBaseMessage(transmuter.getOriginType(), transmuter.getTargetType());
    }

    private <REPO extends OTRepository> OTABuilder(REPO repository, Class<ORIGIN> originType, Class<TARGET> targetType) {
        this.originType = originType;
        this.targetType = targetType;
        this.repository = repository;
        this.BASE_MESSAGE = getBaseMessage(originType, targetType);
    }

    /**
     * Create an {@link OTABuilder} instance from origin's and target's mapper
     *
     * @param mapper origin's and target's mapper
     * @param <ORIGIN> origin object
     * @param <TARGET> target object
     * @return {@link OTABuilder} instance
     */
    public static <ORIGIN, TARGET> OTABuilder<ORIGIN, TARGET> instance(OTRepository repository, OTMapper<ORIGIN, TARGET> mapper) {
        return new OTABuilder<>(repository, mapper);
    }

    /**
     * @see #instance(OTRepository, OTMapper)
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
    public static <ORIGIN, TARGET> OTABuilder<ORIGIN, TARGET> instance(OTRepository repository, Class<ORIGIN> origin, Class<TARGET> target) {
        return new OTABuilder<>(repository, origin, target);
    }

    /**
     * @see #instance(OTRepository, Class, Class)
     */
    public static <ORIGIN, TARGET> OTABuilder<ORIGIN, TARGET> instance(Class<ORIGIN> origin, Class<TARGET> target) {
        return instance(null, origin, target);
    }

    /**
     * Build the target's object from origin's object.
     * Case when origin is null return null.
     * Case when origin and target are of the same type return origin.
     * Case when exist converter for types then convert origin to target.
     * Case when mapper is used and target is null try to create an instance of it.
     * Case when the inner complex objects has specific transmuter {@link OTTransmuter} defined by user,
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
        else if (Objects.equals(originType, targetType) || primitivable(originType, targetType)) {
            return (TARGET) origin;
        }
        else {
           return transmute(origin, target, deepAutomatedBuild);
        }
    }

    protected TARGET transmute(ORIGIN origin, TARGET target, boolean deepAutomatedBuild) {
        if (repository != null && repository.exists(originType, targetType)) {
            OTTransmuter<ORIGIN, TARGET> transmuter = repository.get(originType, targetType);
            if (transmuter instanceof OTConverter) {
                return transmute((OTConverter<ORIGIN, TARGET>) transmuter, origin);
            } else if (transmuter instanceof OTMapper) {
                return transmute((OTMapper<ORIGIN, TARGET>) transmuter, origin, target, deepAutomatedBuild);
            } else {
                throw new OTException(String.format(BASE_MESSAGE, "TRANSMUTER TYPE NOT SUPPORTED"));
            }
        }
        else {
            OTMapper<ORIGIN, TARGET> mapper = new OTMapperFactory(repository).mapperForClasses(originType, targetType);
            return transmute(mapper, origin, target, deepAutomatedBuild);
        }
    }

    private TARGET transmute(OTConverter<ORIGIN, TARGET> converter, ORIGIN origin) {
        return converter.convert(origin);
    }

    private TARGET transmute(OTMapper<ORIGIN, TARGET> mapper, ORIGIN origin, TARGET target, boolean deepAutomatedBuild) {
        final TARGET newTarget = target != null ? target : createInstance(targetType, BASE_MESSAGE);
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

    /**
     * @param mapper for origin {@param <O>} and target {@param <T>}
     *
     * @return {@link Map} of {@link PropertyDescriptor} by mapper {@link OTMapper}
     */
    private <O, T> Map<PropertyDescriptor, PropertyDescriptor> verifyAndGetMapping(OTMapper<O, T> mapper) {
        final String message = "VERIFY MAPPING " + getBaseMessage(mapper.getOriginType(), mapper.getTargetType());
        Map<PropertyDescriptor, PropertyDescriptor> mappedProperties;
        try {
            mappedProperties = mapper.getMappedProperties();
        } catch (Exception cause) {
            String failedMessage = String.format(message, "FAILED. CAUSE: ");
            throw new OTException(failedMessage + cause.getMessage(), cause);
        }
        logger.info(String.format(message, "SUCCESS"));
        return mappedProperties;
    }

    /**
     * Write origin's property's value to target's property if they are of the same type or if they are attributable
     * to the same primitive type; otherwise, if {@param deepAutomatedMap} is true or if a transmuter defined
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
        Object originValue = origin != null ? originProperty.getReadMethod().invoke(origin) : null;
        if (originValue != null) {
            Object targetValue = null;
            Class<?> originPropertyType = originProperty.getPropertyType();
            Class<?> targetPropertyType = targetProperty.getPropertyType();
            if (Objects.equals(originPropertyType, targetPropertyType) || primitivable(originPropertyType, targetPropertyType)) {
                targetProperty.getWriteMethod().invoke(target, (targetValue = originValue));
            } else if (deepAutomatedBuild || (repository != null && repository.exists(originPropertyType, targetPropertyType))) {
                OTABuilder builder = instance(repository, originPropertyType, targetPropertyType);
                targetValue = builder.build(originValue, deepAutomatedBuild);
                targetProperty.getWriteMethod().invoke(target, targetValue);
            }
            if (!assertPropertyWrited(originValue, target, targetProperty, targetValue)) {
                String message = String.format(getBaseMessage(originProperty, targetProperty), "WRITE PROPERTY FAILED! VALUE FROM ORIGIN = " + originValue + " - ACTUAL TARGET VALUE = " + targetValue + " - EXCLUDE FIELD AND ADD CUSTOM MAPPING");
                throw new OTException(message);
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
        String propertiesMessage = "PROPERTIES " + originProperty.getName() + " -> " + targetProperty.getName() + " OF TYPES " + getBaseMessage(originProperty.getPropertyType(), targetProperty.getPropertyType());
        return String.format(BASE_MESSAGE, propertiesMessage);
    }

    private boolean assertPropertyWrited(Object originValue, TARGET target, PropertyDescriptor targetProperty, Object targetValue) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (originValue != null) {
            Object targetValueReaded = targetProperty.getReadMethod() != null ? targetProperty.getReadMethod().invoke(target) : targetValue;
            return targetValueReaded != null;
        }
        return targetValue == null;
    }

}