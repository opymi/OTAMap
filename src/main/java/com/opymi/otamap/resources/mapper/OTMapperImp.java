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

package com.opymi.otamap.resources.mapper;

import com.opymi.otamap.beans.PropertyCustomNameDescriptor;
import com.opymi.otamap.beans.PropertyMapDescriptor;
import com.opymi.otamap.entry.OTCustomMapperOperation;
import com.opymi.otamap.entry.OTOperativeMapper;
import com.opymi.otamap.entry.resources.JTypeEvaluator;
import com.opymi.otamap.entry.resources.TypeScanner;
import com.opymi.otamap.exceptions.CustomizeMappingException;
import com.opymi.otamap.exceptions.OTException;

import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Mapper of the {@param <ORIGIN>} to {@param <TARGET>}
 *
 * @author Antonino Verde
 * @since 1.0
 */
public class OTMapperImp<ORIGIN, TARGET> implements OTOperativeMapper<ORIGIN, TARGET> {
    private final String ERROR_MESSAGE = "%s: %s %s! EXCLUDE FIELD AND ADD CUSTOM MAPPING";

    private final JTypeEvaluator jTypeEvaluator;
    private final TypeScanner typeScanner;
    private final Class<ORIGIN> origin;
    private final Class<TARGET> target;
    private final Set<String> orginDeclaredProperties;
    private final Set<String> targetDeclaredProperties;
    private final List<PropertyCustomNameDescriptor> customNameDescriptors;
    private final Set<String> excludedFields;
    private OTCustomMapperOperation<ORIGIN, TARGET> OTCustomMapperOperation;

    public OTMapperImp(TypeScanner typeScanner, JTypeEvaluator jTypeEvaluator, Class<ORIGIN> origin, Class<TARGET> target) {
        this.typeScanner = typeScanner;
        this.jTypeEvaluator = jTypeEvaluator;
        this.origin = origin;
        this.target = target;
        this.orginDeclaredProperties = typeScanner.retrieveDeclaredFieldsNames(origin);
        this.targetDeclaredProperties = typeScanner.retrieveDeclaredFieldsNames(target);
        this.excludedFields = new HashSet<>();
        this.customNameDescriptors = new ArrayList<>();
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
        if (!orginDeclaredProperties.contains(originField) || !targetDeclaredProperties.contains(targetField)) {
            throw new CustomizeMappingException("CANNOT CUSTOMIZE NAME MAPPING " + originField + " -> " + targetField + ". CHECK FIELD EXISTENCE!");
        }
        PropertyCustomNameDescriptor customNameDescriptor = new PropertyCustomNameDescriptor(originField, targetField);
        customNameDescriptors.add(customNameDescriptor);
    }

    @Override
    public void excludeField(String field, boolean force) {
        if (!force && !orginDeclaredProperties.contains(field) && !targetDeclaredProperties.contains(field)) {
            throw new CustomizeMappingException("THE FIELD " + field + " DOES NOT EXIST");
        }
        excludedFields.add(field);
    }

    @Override
    public void excludeAllFields() {
        excludedFields.addAll(orginDeclaredProperties);
        excludedFields.addAll(targetDeclaredProperties);
    }

    @Override
    public List<PropertyMapDescriptor> generatePropertyMapDescriptors() {
        Map<String, PropertyDescriptor> targetProperties = typeScanner.retrievePropertyDescriptors(target)
                .stream()
                .collect(Collectors.toMap(PropertyDescriptor::getName, p -> p));

        List<PropertyDescriptor> originProperties = typeScanner.retrievePropertyDescriptors(origin);

        return originProperties.stream()
                .filter(this::isValidForMapDescriptor)
                .map(originProperty -> {
                    assertReadableOrigin(originProperty);

                    String originPropertyName = originProperty.getName();
                    String targetPropertyName = findTargetPropertyNameByOrigin(originPropertyName);

                    PropertyDescriptor targetProperty = targetProperties.get(targetPropertyName);
                    assertValidTargetProperty(targetProperty, originPropertyName);

                    return new PropertyMapDescriptor(originProperty, targetProperty);
                })
                .collect(Collectors.toList());
    }

    /**
     * @param originProperty origin property
     * @return true if origin and relative target is not present in excluded fields
     */
    private boolean isValidForMapDescriptor(PropertyDescriptor originProperty) {
        String originPropertyName = originProperty.getName();
        String targetPropertyName = findTargetPropertyNameByOrigin(originPropertyName);
        return !excludedFields.contains(originPropertyName) || !excludedFields.contains(targetPropertyName);
    }

    /**
     * Check if origin property has valid read method
     * @param originProperty
     */
    private void assertReadableOrigin(PropertyDescriptor originProperty) {
        if (originProperty.getReadMethod() == null) {
            throw new OTException(String.format(ERROR_MESSAGE, origin.getName(), originProperty.getName(), "READ METHOD NOT FOUND"));
        }
    }

    /**
     * Check if target property is present and valid
     * @param targetProperty
     * @param originPropertyName
     */
    private void assertValidTargetProperty(PropertyDescriptor targetProperty, String originPropertyName) {
        if (targetProperty == null) {
            throw new OTException(String.format(ERROR_MESSAGE, origin.getName(), originPropertyName, "MATCH NOT FOUND! DOESN'T EXIST FIELD WITH SAME NAME IN " + target.getName()));
        } else if (targetProperty.getWriteMethod() == null) {
            throw new OTException(String.format(ERROR_MESSAGE, target.getName(), targetProperty.getName(), "WRITE METHOD NOT FOUND"));
        } else if (jTypeEvaluator.isUnsupportedType(targetProperty.getPropertyType())) {
            throw new OTException(String.format(ERROR_MESSAGE, target.getName(), targetProperty.getName(), "TYPE NOT SUPPORTED " + targetProperty.getPropertyType().getSimpleName()));
        }
    }

    private PropertyCustomNameDescriptor findCustomNameDescriptorByOrigin(String originName) {
        return customNameDescriptors.stream()
                .filter(customNameDescriptor -> customNameDescriptor.getOrigin().equals(originName))
                .findAny().orElse(null);
    }

    private String findTargetPropertyNameByOrigin(String originName) {
        PropertyCustomNameDescriptor customNameDescriptor = findCustomNameDescriptorByOrigin(originName);
        return customNameDescriptor != null ? customNameDescriptor.getTarget() : originName;
    }

}
