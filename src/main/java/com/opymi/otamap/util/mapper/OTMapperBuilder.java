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

package com.opymi.otamap.util.mapper;


import com.opymi.otamap.exception.CustomizeMappingException;
import com.opymi.otamap.exception.OTException;
import com.opymi.otamap.util.OTFieldsScanner;

import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that builds the mapper of the {@param <ORIGIN>} to {@param <TARGET>}
 *
 * @author Antonino Verde
 * @since 1.0
 */
public class OTMapperBuilder<ORIGIN, TARGET> {
    private final OTMapperImpl<ORIGIN, TARGET> mapper;

    private OTMapperBuilder(Class<ORIGIN> origin, Class<TARGET> target) {
        this.mapper = new OTMapperImpl<>(origin, target);
    }

    /**
     * Create an {@link OTMapperBuilder} instance from origin's type and target's type
     *
     * @param origin origin's type
     * @param target target's type
     * @return {@link OTMapperBuilder} instance
     */
    public static <ORIGIN, TARGET> OTMapperBuilder<ORIGIN, TARGET> instance(Class<ORIGIN> origin, Class<TARGET> target) {
        return new OTMapperBuilder<>(origin, target);
    }

    /**
     * Customize the mapping with custom behavior
     * @param customMapperOperation implementation of {@link OTCustomMapperOperation} that defines custom operations
     * on fields
     *
     * @return current instance of {@link OTMapperBuilder}
     */
    public OTMapperBuilder<ORIGIN, TARGET> customize(OTCustomMapperOperation<ORIGIN, TARGET> customMapperOperation) {
        mapper.addCustomOperation(customMapperOperation);
        return this;
    }

    /**
     * Customize the mapping for fields with different names
     * @param originField origin's field name
     * @param targetField target's field name
     *
     * @return current instance of {@link OTMapperBuilder}
     */
    public OTMapperBuilder<ORIGIN, TARGET> customize(String originField, String targetField) {
        mapper.addCutomNameMapping(originField, targetField);
        return this;
    }

    /**
     * Customize the mapping for fields with different names
     * @param originTargetFieldMap Map that contains originField as key and targetField as value
     *
     * @return current instance of {@link OTMapperBuilder}
     */
    public OTMapperBuilder<ORIGIN, TARGET> customize(Map<String, String> originTargetFieldMap) {
        originTargetFieldMap.forEach(mapper::addCutomNameMapping);
        return this;
    }

    /**
     * Exclude target's field from the mapping
     * @param field target's field name
     * @param force if true doesn't check if the field exists
     *
     * @return current instance of {@link OTMapperBuilder}
     */
    public OTMapperBuilder<ORIGIN, TARGET> excludeField(String field, boolean force) {
        mapper.excludeField(field, force);
        return this;
    }

    //TODO write documentation
    public OTMapperBuilder<ORIGIN, TARGET> excludeField(String field) {
        excludeField(field, false);
        return this;
    }

    //TODO write documentation
    public OTMapperBuilder<ORIGIN, TARGET> excludeAllFields() {
        mapper.excludeAllFields();
        return this;
    }

    /**
     * @return instance of the current {@link OTMapper}
     */
    public OTMapper<ORIGIN, TARGET> build() {
        return mapper;
    }

    /**
     * Implementation of {@link OTMapper}
     *
     * @param <ORIGIN>
     * @param <TARGET>
     */
    private static class OTMapperImpl<ORIGIN, TARGET> implements OTMapper<ORIGIN, TARGET> {
        private static final List<Class<?>> UNSUPPORTED_TYPES;
        static {
            UNSUPPORTED_TYPES = new ArrayList<>();
            UNSUPPORTED_TYPES.add(Collection.class);
            UNSUPPORTED_TYPES.add(Map.class);
        }

        private final Class<ORIGIN> origin;
        private final Class<TARGET> target;
        private final Set<String> orginDeclaredProperties;
        private final Set<String> targetDeclaredProperties;
        private final Map<String, String> customNameMapping;
        private final Set<String> excludedFields;
        private OTCustomMapperOperation<ORIGIN, TARGET> OTCustomMapperOperation;

        private OTMapperImpl(Class<ORIGIN> origin, Class<TARGET> target) {
            if (origin == null || target == null || Objects.equals(origin, target)) {
                throw new IllegalArgumentException("ORIGIN AND TARGET OBJECTS MUST BE NOT NULL AND NOT EQUALS");
            }
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
                    } else if (targetProperty.getPropertyType().isArray() || UNSUPPORTED_TYPES.stream().anyMatch(type -> type.isAssignableFrom(targetProperty.getPropertyType()))) {
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

        /**
         * Add the custom behavior
         * @param customMapperOperation implementation of {@link OTCustomMapperOperation} that defines custom operations
         * on fields
         */
        private void addCustomOperation(OTCustomMapperOperation<ORIGIN, TARGET> customMapperOperation) {
            this.OTCustomMapperOperation = customMapperOperation;
        }

        /**
         * Add custom mapping for fields with different names
         * @param originField origin's field name
         * @param targetField target's field name
         */
        private void addCutomNameMapping(String originField, String targetField) {
            existField(targetDeclaredProperties, targetField);
            existField(orginDeclaredProperties, originField);
            customNameMapping.put(originField, targetField);
        }

        /**
         * Exclude field from the mapping
         * @param field field's name
         */
        private void excludeField(String field, boolean force) {
            if (!force) {
                existField(field);
            }
            excludedFields.add(field);
        }

        /**
         * Exclude field from the mapping
         */
        private void excludeAllFields() {
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

}