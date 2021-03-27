package com.opymi.otamap.internal;

import com.opymi.otamap.util.OTMapper;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.util.HashMap;
import java.util.Map;

/**
 * Repository of {@link OTMapper} defined by user
 *
 * @author Antonino Verde
 * @since 1.0
 */
public class OTMapperRepository {

    static  <ORIGIN, TARGET> OTMapper<ORIGIN, TARGET> retrieve(Class<ORIGIN> origin, Class<TARGET> target) {
        //TODO implement
        return null;
    }

    public static  <ORIGIN, TARGET> boolean exists(Class<ORIGIN> origin, Class<TARGET> target) {
        //TODO implement
        return false;
    }

    static <ORIGIN, TARGET> void store(OTMapper<ORIGIN, TARGET> mapper) {
        //TODO implement
    }

    private static  <ORIGIN, TARGET> String retrieveKey(Class<ORIGIN> origin, Class<TARGET> target) {
        return origin.getName() + "_" + target.getName();
    }
}
