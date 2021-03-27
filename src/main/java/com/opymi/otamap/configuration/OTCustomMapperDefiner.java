package com.opymi.otamap.configuration;

import com.opymi.otamap.util.OTMapper;

/**
 * Interface that needs to be implemented by classes that defines custom mapper
 * @param <ORIGIN> origin type
 * @param <TARGET> target type
 *
 * @author Antonino Verde
 * @since 1.0
 */
public interface OTCustomMapperDefiner<ORIGIN, TARGET> {

    OTMapper<ORIGIN, TARGET> define();

}
