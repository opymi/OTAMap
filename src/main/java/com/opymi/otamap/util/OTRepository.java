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
import com.opymi.otamap.util.converter.OTConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * Repository of {@link OTTransmuter} defined by user
 *
 * @author Antonino Verde
 * @since 1.0
 */
public class OTRepository {
    private final Map<String, OTTransmuter<?, ?>> repository = new HashMap<>();

    /**
     * @param origin origin's type
     * @param target target's type
     * @return true if exists transmuter for types
     */
    public <ORIGIN, TARGET> boolean exists(Class<ORIGIN> origin, Class<TARGET> target) {
        return repository.containsKey(retrieveKey(origin, target));
    }

    /**
     * @param origin origin's type
     * @param target target's type
     * @return true if exist converter for types
     */
    public <ORIGIN, TARGET> boolean existConverter(Class<ORIGIN> origin, Class<TARGET> target) {
        return repository.get(retrieveKey(origin, target)) instanceof OTConverter;
    }

    /**
     * store {@param transmuter}
     */
    public <ORIGIN, TARGET, TRANSMUTER extends OTTransmuter<ORIGIN, TARGET>> void store(TRANSMUTER transmuter) {
        if (transmuter == null) {
            throw new OTException("TRANSMUTER IS NULL");
        }
        Class<ORIGIN> origin = transmuter.getOriginType();
        Class<TARGET> target = transmuter.getTargetType();
        if (exists(origin, target)) {
            throw new OTException("TRANSMUTER ALREADY EXISTS FOR CLASSES: " + origin.getName() + " " + target.getName());
        }
        repository.put(retrieveKey(origin, target), transmuter);
    }

    /**
     * store transmuter by {@param definer}
     */
    public <ORIGIN, TARGET, TRANSMUTER extends OTTransmuter<ORIGIN, TARGET>> void store(OTCustomTransmuterDefiner<ORIGIN, TARGET, TRANSMUTER> definer) {
        if (definer != null) {
            store(definer.define(this));
        }
    }

    /**
     * @param origin origin's type
     * @param target target's type
     * @return transmuter for types
     */
    @SuppressWarnings("unchecked")
    public <ORIGIN, TARGET, TRANSMUTER extends OTTransmuter<ORIGIN, TARGET>> TRANSMUTER get(Class<ORIGIN> origin, Class<TARGET> target) {
        String key = retrieveKey(origin, target);
        if (repository.containsKey(key)) {
            return (TRANSMUTER) repository.get(key);
        }
        return null;
    }

    /**
     * remove transmuter by types
     */
    public <ORIGIN, TARGET> void remove(Class<ORIGIN> origin, Class<TARGET> target) {
        repository.remove(retrieveKey(origin, target));
    }

    /**
     * remove transmuter by {@param definer}
     */
    public <ORIGIN, TARGET, TRANSMUTER extends OTTransmuter<ORIGIN, TARGET>> void remove(OTCustomTransmuterDefiner<ORIGIN, TARGET, TRANSMUTER> definer) {
        if (definer != null) {
            TRANSMUTER transmuter = definer.define(this);
            remove(transmuter.getOriginType(), transmuter.getTargetType());
        }
    }

    /**
     * @return key for types {@param origin} and {@param target}
     */
    private static <ORIGIN, TARGET> String retrieveKey(Class<ORIGIN> origin, Class<TARGET> target) {
        return origin.getName() + "_" + target.getName();
    }

}
