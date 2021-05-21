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

import com.opymi.otamap.exception.OTException;
import com.opymi.otamap.util.mapper.OTCustomMapperDefiner;
import com.opymi.otamap.util.mapper.OTMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Repository of {@link OTMapper} defined by user
 *
 * @author Antonino Verde
 * @since 1.0
 */
public class OTRepository {

    private final Map<String, OTMapper<?, ?>> repository = new HashMap<>();

    //TODO write documentation
    public <ORIGIN, TARGET> boolean exists(Class<ORIGIN> origin, Class<TARGET> target) {
        return repository.containsKey(retrieveKey(origin, target));
    }

    //TODO write documentation
    public <ORIGIN, TARGET> void store(OTMapper<ORIGIN, TARGET> mapper) {
        if (mapper == null) {
            throw new OTException("MAPPER IS NULL");
        }
        Class<ORIGIN> origin = mapper.getOriginClass();
        Class<TARGET> target = mapper.getTargetClass();
        if (exists(origin, target)) {
            throw new OTException("MAPPER ALREADY EXISTS FOR CLASSES: " + origin.getName() + " " + target.getName());
        }
        repository.put(retrieveKey(origin, target), mapper);
    }

    //TODO write documentation
    public <ORIGIN, TARGET> void store(OTCustomMapperDefiner<ORIGIN, TARGET> definer) {
        if (definer != null) {
            store(definer.define(this));
        }
    }

    //TODO write documentation
    @SuppressWarnings("unchecked")
    public <ORIGIN, TARGET> OTMapper<ORIGIN, TARGET> get(Class<ORIGIN> origin, Class<TARGET> target) {
        String key = retrieveKey(origin, target);
        if (repository.containsKey(key)) {
            return (OTMapper<ORIGIN, TARGET>) repository.get(key);
        }
        return null;
    }

    //TODO write documentation
    public <ORIGIN, TARGET> void remove(Class<ORIGIN> origin, Class<TARGET> target) {
        repository.remove(retrieveKey(origin, target));
    }

    //TODO write documentation
    public <ORIGIN, TARGET> void remove(OTCustomMapperDefiner<ORIGIN, TARGET> definer) {
        if (definer != null) {
            OTMapper<ORIGIN, TARGET> mapper = definer.define(this);
            remove(mapper.getOriginClass(), mapper.getTargetClass());
        }
    }

    //TODO write documentation
    private static <ORIGIN, TARGET> String retrieveKey(Class<ORIGIN> origin, Class<TARGET> target) {
        return origin.getName() + "_" + target.getName();
    }

}
