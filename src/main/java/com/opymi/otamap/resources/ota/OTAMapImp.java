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

package com.opymi.otamap.resources.ota;

import com.opymi.otamap.entry.*;
import com.opymi.otamap.entry.resources.JTypeEvaluator;
import com.opymi.otamap.entry.resources.OTAMapProvider;
import com.opymi.otamap.entry.resources.OTAMessageFormatter;
import com.opymi.otamap.exceptions.AccessPropertyException;
import com.opymi.otamap.exceptions.CreateInstanceException;
import com.opymi.otamap.exceptions.OTException;
import com.opymi.otamap.entry.resources.OTMapperBuilderProvider;
import com.opymi.otamap.beans.PropertyMapDescriptor;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Core engine to build a {@param <TARGET>} object from an {@param <ORIGIN>} object
 *
 * @param <ORIGIN>
 * @param <TARGET>
 *
 * @author Antonino Verde
 * @since 1.0
 */
public class OTAMapImp<ORIGIN, TARGET> implements OTAMap<ORIGIN, TARGET> {
    private static final Logger logger = Logger.getLogger(OTAMapImp.class.getSimpleName());

    private final OTRepository repository;
    private final Class<ORIGIN> originType;
    private final Class<TARGET> targetType;

    private JTypeEvaluator jTypeEvaluator;
    private OTAMessageFormatter messageFormatter;

    public OTAMapImp(OTRepository repository, Class<ORIGIN> originType, Class<TARGET> targetType) {
        this.originType = originType;
        this.targetType = targetType;
        this.repository = repository;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final TARGET map(ORIGIN origin, TARGET target, boolean deepAutomatedMapping) {
        if (origin == null) {
            return null;
        }
        else if (Objects.equals(originType, targetType) || jTypeEvaluator.isPrimitivable(originType, targetType)) {
            return (TARGET) origin;
        }
        else {
           return transmute(origin, target, deepAutomatedMapping);
        }
    }

    @Override
    public final TARGET map(ORIGIN origin, TARGET target) {
        return map(origin, target, false);
    }


    @Override
    public final TARGET map(ORIGIN origin) {
        return map(origin, null, false);
    }

    @Override
    public final TARGET map(ORIGIN origin, boolean deepAutomatedMapping) {
        return map(origin, null, deepAutomatedMapping);
    }

    /**
     * Transmute origin to target
     *
     * Case when converter exists for types then uses it.
     * Case when mapper exists or is possibile to create a mapper for types then uses it.
     *
     * @throws OTException if transmuter type is not supported
     * @return target object
     */
    protected TARGET transmute(ORIGIN origin, TARGET target, boolean deepAutomatedBuild) {
        if (repository != null && repository.exists(originType, targetType)) {
            OTTransmuter<ORIGIN, TARGET> transmuter = repository.get(originType, targetType);
            if (transmuter instanceof OTConverter) {
                return ((OTConverter<ORIGIN, TARGET>) transmuter).convert(origin);
            } else if (transmuter instanceof OTMapper) {
                return transmute((OTMapper<ORIGIN, TARGET>) transmuter, origin, target, deepAutomatedBuild);
            } else {
                String errorMessage = messageFormatter.format(originType, targetType, "TRANSMUTER TYPE NOT SUPPORTED");
                throw new OTException(errorMessage);
            }
        }
        else {
            OTMapperBuilderProvider builderProvider = ResourceProvider.getResource(OTMapperBuilderProvider.class);
            OTMapper<ORIGIN, TARGET> mapper = builderProvider.getBuilder(originType, targetType).getMapper();
            return transmute(mapper, origin, target, deepAutomatedBuild);
        }
    }

    /**
     * Transmute origin to target using {@link OTMapper}
     *
     * @param mapper
     * @param origin
     * @param target
     * @param deepAutomatedMap
     * @return transmuted target instance
     */
    private TARGET transmute(OTMapper<ORIGIN, TARGET> mapper, ORIGIN origin, TARGET target, boolean deepAutomatedMap) {
        final TARGET newTarget = target != null ? target : createInstance(targetType);

        executeDefaultMapping(mapper, origin, newTarget, deepAutomatedMap);
        executeCustomMapping(mapper, origin, newTarget);

        return newTarget;
    }

    /**
     * Execute default mapping of origin and target mapped properties
     *
     * @param mapper
     * @param origin
     * @param target
     * @param deepAutomatedMap
     */
    private void executeDefaultMapping(OTMapper<ORIGIN, TARGET> mapper, ORIGIN origin, TARGET target, boolean deepAutomatedMap) {
        List<PropertyMapDescriptor> propertyMapDescriptors = generatePropertyMapDescriptors(mapper);
        propertyMapDescriptors.forEach(propertyMapDescriptor -> {
            PropertyDescriptor originProperty = propertyMapDescriptor.getOrigin();
            PropertyDescriptor targetProperty = propertyMapDescriptor.getTarget();
            try {
                Object originValue = origin != null ? originProperty.getReadMethod().invoke(origin) : null;
                if(originValue != null) {
                    Object targetValue = mapTargetByOrigin(originValue, targetProperty.getPropertyType(), deepAutomatedMap);
                    targetProperty.getWriteMethod().invoke(target, targetValue);
                }
            } catch (IllegalAccessException | InvocationTargetException cause) {
                String detailMessage = "CANNOT READ ORIGIN'S PROPERTY " + originProperty.getName() + " OR CANNOT WRITE TARGET'S PROPERTY " + targetProperty.getName();
                String errorMessage = messageFormatter.format(originType, targetType, detailMessage);
                throw new AccessPropertyException(errorMessage, cause);
            }
        });
    }

    /**
     * Execute custom mapping of origin and target properties
     *
     * @param mapper
     * @param origin
     * @param target
     */
    private void executeCustomMapping(OTMapper<ORIGIN, TARGET> mapper, ORIGIN origin, TARGET target) {
        OTCustomMapperOperation<ORIGIN, TARGET> OTCustomMapperOperation = mapper.getCustomMapper();
        if (OTCustomMapperOperation != null) {
            OTCustomMapperOperation.customMap(origin, target);
        }
    }

    /**
     * Create a target object like an exact copy of the origin object
     *
     * @param originPropertyValue
     * @param targetType
     * @param deepAutomatedClone
     *
     * @return target object
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private Object mapTargetByOrigin(Object originPropertyValue, Class<?> targetType, boolean deepAutomatedClone) {
        Class<?> originPropertyType = originPropertyValue.getClass();

        if (Objects.equals(originPropertyType, targetType) || jTypeEvaluator.isPrimitivable(originPropertyType, targetType)) {
            return originPropertyValue;
        }
        else if (deepAutomatedClone || (repository != null && repository.exists(originPropertyType, targetType))) {
            OTAMapProvider otaMapProvider = ResourceProvider.getResource(OTAMapProvider.class);
            OTAMap otaMap = otaMapProvider.getOTAMap(repository, originPropertyType, targetType);
            return otaMap.map(originPropertyValue, deepAutomatedClone);
        }
        return null;
    }

    /**
     * @param mapper for origin {@param <O>} and target {@param <T>}
     *
     * @return {@link Map} of {@link PropertyDescriptor} by mapper {@link OTMapper}
     */
    private <O, T> List<PropertyMapDescriptor> generatePropertyMapDescriptors(OTMapper<O, T> mapper) {
        String verifyMessage = messageFormatter.format(mapper.getOriginType(), mapper.getTargetType(), "VERIFY MAPPING");
        logger.info(verifyMessage);
        try {
            return mapper.generatePropertyMapDescriptors();
        } catch (Exception cause) {
            String failedMessage = verifyMessage + " FAILED. CAUSE: " + cause.getMessage();
            throw new OTException(failedMessage, cause);
        }
    }

    /**
     * @return an instance of {@param type} class
     */
    private <T> T createInstance(Class<T> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException | IllegalArgumentException | ExceptionInInitializerError cause) {
            String errorMessage = messageFormatter.format(originType, targetType, "CANNOT CREATE INSTANCE OF " + type.getName());
            throw new CreateInstanceException(errorMessage, cause);
        }
    }

    public void setjTypeEvaluator(JTypeEvaluator jTypeEvaluator) {
        this.jTypeEvaluator = jTypeEvaluator;
    }

    public void setMessageFormatter(OTAMessageFormatter messageFormatter) {
        this.messageFormatter = messageFormatter;
    }

}
