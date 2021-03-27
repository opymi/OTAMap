package com.opymi.otamap.util;

import java.beans.PropertyDescriptor;
import java.util.Map;

/**
 * Interface that defines the mapper of the {@param <ORIGIN>} to {@param <TARGET>}
 *
 * @author Antonino Verde
 * @since 1.0
 */
public interface OTMapper<ORIGIN, TARGET> {

    /**
     * @return origin type
     */
    Class<ORIGIN> getOriginClass();

    /**
     * @return target type
     */
    Class<TARGET> getTargetClass();

    /**
     * @return map that contains the mapped properties. Origin's property as key and Target's property as value.
     */
    Map<PropertyDescriptor, PropertyDescriptor> getMappedProperties();

    /**
     * @return {@link OTCustomMapperOperation} custom behavior for the mapping
     */
    OTCustomMapperOperation<ORIGIN, TARGET> getCustomMapper();
}
