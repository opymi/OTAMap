package com.opymi.otamap.util;


import com.opymi.otamap.exception.CustomizeMappingException;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
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
        mapper.addCutomNameMapping(targetField, originField);
        return this;
    }

    /**
     * Exclude target's field from the mapping
     * @param field target's field name
     *
     * @return current instance of {@link OTMapperBuilder}
     */
    public OTMapperBuilder<ORIGIN, TARGET> excludeTargetField(String field) {
        mapper.excludeTargetField(field);
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
        private final Class<ORIGIN> origin;
        private final Class<TARGET> target;
        private final Set<String> orginDeclaredProperties;
        private final Set<String> targetDeclaredProperties;

        private final Map<String, String> customNameMapping = new HashMap<>();
        private final Set<String> excludedFields = new HashSet<>();
        private OTCustomMapperOperation<ORIGIN, TARGET> OTCustomMapperOperation;

        private OTMapperImpl(Class<ORIGIN> origin, Class<TARGET> target) {
            if (origin == null || target == null || Objects.equals(origin, target)) {
                throw new IllegalArgumentException("ORIGIN AND TARGET OBJECTS MUST BE NOT NULL AND NOT EQUALS");
            }
            this.origin = origin;
            this.target = target;
            this.orginDeclaredProperties = getDeclaredFields(origin);
            this.targetDeclaredProperties = getDeclaredFields(target);
        }


        @Override
        public Map<PropertyDescriptor, PropertyDescriptor> getMappedProperties() {
            Map<String, PropertyDescriptor> originProperties = retrievePropertyDescriptors(origin).stream()
                    .collect(Collectors.toMap(PropertyDescriptor::getName, p -> p));

            Map<PropertyDescriptor, PropertyDescriptor> mappedProperties = new HashMap<>();

            retrievePropertyDescriptors(target).stream()
                    .filter(property -> {
                        String name = property.getName();
                        return targetDeclaredProperties.contains(name) && !excludedFields.contains(name) && property.getWriteMethod() != null;
                    })
                    .forEach(targetProperty -> {
                        String targetPropertyName = targetProperty.getName();
                        String originPropertyName = customNameMapping.getOrDefault(targetPropertyName, targetPropertyName);
                        PropertyDescriptor originProperty = originProperties.get(originPropertyName);
                        if (originProperty != null && originProperty.getReadMethod() != null) {
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
        public Class<ORIGIN> getOriginClass() {
            return origin;
        }

        @Override
        public Class<TARGET> getTargetClass() {
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
        private void addCutomNameMapping(String targetField, String originField) {
            existField(targetDeclaredProperties, targetField);
            existField(orginDeclaredProperties, originField);
            customNameMapping.put(targetField, originField);
        }

        /**
         * Exclude target's field from the mapping
         * @param field target's field name
         */
        private void excludeTargetField(String field) {
            existField(targetDeclaredProperties, field);
            excludedFields.add(field);
        }

        /**
         * @return list of {@link PropertyDescriptor} of the type {@param type}
         */
        private List<PropertyDescriptor> retrievePropertyDescriptors(Class<?> type) {
            BeanInfo beanInfo;
            try {
                beanInfo = Introspector.getBeanInfo(type);
            } catch (IntrospectionException exception) {
                throw new RuntimeException("CANNOT RETRIEVE BEAN INFO", exception);
            }

            return Arrays.asList(beanInfo.getPropertyDescriptors());
        }

        /**
         * @return declared fields of {@param clazz}
         */
        private Set<String> getDeclaredFields(Class<?> clazz) {
            return Arrays.stream(clazz.getDeclaredFields())
                    .map(Field::getName)
                    .collect(Collectors.toSet());
        }

        /**
         * Check if the field {@param toCheck} exists in {@param properties} Set of all declared fields
         */
        private void existField(Set<String> properties, String toCheck) {
            if (!properties.contains(toCheck)) {
                throw new CustomizeMappingException("THE FIELD " + toCheck + " DOES NOT EXIST");
            }
        }
    }

}
