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

package com.opymi.otamap.resource.mapper;

import com.opymi.otamap.entry.OTCustomMapperOperation;
import com.opymi.otamap.entry.OTOperativeMapper;
import com.opymi.otamap.entry.resource.JTypeEvaluator;
import com.opymi.otamap.exception.CustomizeMappingException;
import com.opymi.otamap.exception.OTException;
import com.opymi.otamap.resource.util.OTFieldsScanner;

import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Mapper of the {@param <ORIGIN>} to {@param <TARGET>}
 *
 * @author Antonino Verde
 * @since 1.0
 */
class OTMapperImp<ORIGIN, TARGET> implements OTOperativeMapper<ORIGIN, TARGET> {
    private final JTypeEvaluator jTypeEvaluator;
    private final Class<ORIGIN> origin;
    private final Class<TARGET> target;
    private final Set<String> orginDeclaredProperties;
    private final Set<String> targetDeclaredProperties;
    private final Map<String, String> customNameMapping;
    private final Set<String> excludedFields;
    private OTCustomMapperOperation<ORIGIN, TARGET> OTCustomMapperOperation;

    OTMapperImp(JTypeEvaluator jTypeEvaluator, Class<ORIGIN> origin, Class<TARGET> target) {
        this.jTypeEvaluator = jTypeEvaluator;
        this.origin = origin;
        this.target = target;
        this.orginDeclaredProperties = getDeclaredFields(origin);
        this.targetDeclaredProperties = getDeclaredFields(target);
        this.excludedFields = new HashSet<>();
        this.customNameMapping = new HashMap<>();
    }

    @Override
    public Map<PropertyDescriptor, PropertyDescriptor> getMappedProperties() {
        final String errorMessage = "%s: %s %s! EXCLUDE FIELD AND ADD CUSTOM MAPPING";

        Map<String, PropertyDescriptor> targetProperties = OTFieldsScanner.retrievePropertyDescriptors(target).stream().collect(Collectors.toMap(PropertyDescriptor::getName, p -> p));
        Map<PropertyDescriptor, PropertyDescriptor> mappedProperties = new HashMap<>();

        OTFieldsScanner.retrievePropertyDescriptors(origin).stream().filter(property -> !excludedFields.contains(property.getName())).forEach(originProperty -> {
            String originPropertyName = originProperty.getName();
            if (originProperty.getReadMethod() == null) {
                throw new OTException(String.format(errorMessage, origin.getName(), originPropertyName, "READ METHOD NOT FOUND"));
            }

            String targetPropertyName = customNameMapping.getOrDefault(originPropertyName, originPropertyName);
            if (!excludedFields.contains(targetPropertyName)) {
                PropertyDescriptor targetProperty = targetProperties.get(targetPropertyName);
                if (targetProperty == null) {
                    throw new OTException(String.format(errorMessage, origin.getName(), originPropertyName, "MATCH NOT FOUND! DOESN'T EXIST FIELD WITH SAME NAME IN " + target.getName()));
                } else if (targetProperty.getWriteMethod() == null) {
                    throw new OTException(String.format(errorMessage, target.getName(), targetPropertyName, "WRITE METHOD NOT FOUND"));
                } else if (targetProperty.getPropertyType().isArray() || jTypeEvaluator.isUnsupportedType(targetProperty.getPropertyType())) {
                    throw new OTException(String.format(errorMessage, target.getName(), targetPropertyName, "TYPE NOT SUPPORTED " + targetProperty.getPropertyType().getSimpleName()));
                }
                mappedProperties.put(originProperty, targetProperty);
            }
        });

        return mappedProperties;
    }

    @Override
    public OTCustomMapperOperation<ORIGIN, TARGET> getCustomMapper() {
        return OTCustomMapperOperation;
    }

    @Override
    public Class<ORIGIN> getOriginType() {
        return origin;
    }

    @Override
    public Class<TARGET> getTargetType() {
        return target;
    }

    @Override
    public void setCustomOperation(OTCustomMapperOperation<ORIGIN, TARGET> customMapperOperation) {
        this.OTCustomMapperOperation = customMapperOperation;
    }

    @Override
    public void addCutomNameMapping(String originField, String targetField) {
        existField(targetDeclaredProperties, targetField);
        existField(orginDeclaredProperties, originField);
        customNameMapping.put(originField, targetField);
    }

    @Override
    public void excludeField(String field, boolean force) {
        if (!force) {
            existField(field);
        }
        excludedFields.add(field);
    }

    @Override
    public void excludeAllFields() {
        excludedFields.addAll(orginDeclaredProperties);
        excludedFields.addAll(targetDeclaredProperties);
    }

    /**
     * @return declared fields of {@param clazz}
     */
    private Set<String> getDeclaredFields(Class<?> clazz) {
        return new HashSet<>(OTFieldsScanner.findFields(clazz));
    }

    /**
     * Check if the field {@param toCheck} exists in {@param properties} Set of all declared fields
     */
    private void existField(Set<String> properties, String toCheck) {
        if (!properties.contains(toCheck)) {
            throw new CustomizeMappingException("THE FIELD " + toCheck + " DOES NOT EXIST");
        }
    }

    private void existField(String toCheck) {
        if (!orginDeclaredProperties.contains(toCheck) && !targetDeclaredProperties.contains(toCheck)) {
            throw new CustomizeMappingException("THE FIELD " + toCheck + " DOES NOT EXIST");
        }
    }

}
