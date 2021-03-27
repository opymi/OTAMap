package com.opymi.otamap.internal;

import com.opymi.otamap.util.OTMapper;
import com.opymi.otamap.util.OTMapperBuilder;

/**
 * Factory of {@link OTMapper}
 *
 * @author Antonino Verde
 * @since 1.0
 */
public class OTMapperFactory {

    /**
     * Check if mapper defined for types exists and return it or default mapper
     *
     * @param origin origin type
     * @param target target type
     * @return instance of {@link OTMapper} for types {@param origin} and {@param target}
     */
    public static  <ORIGIN, TARGET> OTMapper<ORIGIN, TARGET> mapperForClasses(Class<ORIGIN> origin, Class<TARGET> target) {
        return OTMapperRepository.exists(origin, target)
                ? OTMapperRepository.retrieve(origin, target)
                : OTMapperBuilder.instance(origin, target).build();
    }

}
