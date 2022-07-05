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

package com.opymi.otamap.resource.repository;

import com.opymi.otamap.entry.OTRepository;
import com.opymi.otamap.exception.OTException;
import com.opymi.otamap.entry.OTCustomTransmuterDefiner;
import com.opymi.otamap.entry.OTTransmuter;

import java.util.HashMap;
import java.util.Map;

/**
 * Repository of {@link OTTransmuter} defined by user
 *
 * @author Antonino Verde
 * @since 1.0
 */
public class OTRepositoryImp implements OTRepository {
    private final Map<String, OTTransmuter<?, ?>> repository = new HashMap<>();


    @Override
    public <ORIGIN, TARGET> boolean exists(Class<ORIGIN> origin, Class<TARGET> target) {
        String key = composeKey(origin, target);
        return repository.containsKey(key);
    }

    @Override
    public <ORIGIN, TARGET, TRANSMUTER extends OTTransmuter<ORIGIN, TARGET>> void store(TRANSMUTER transmuter) {
        checkTransmuter(transmuter);

        String key = composeKey(transmuter.getOriginType(), transmuter.getTargetType());
        repository.put(key, transmuter);
    }

   @Override
    public <ORIGIN, TARGET, TRANSMUTER extends OTTransmuter<ORIGIN, TARGET>> void store(OTCustomTransmuterDefiner<ORIGIN, TARGET, TRANSMUTER> definer) {
        if (definer != null) {
            store(definer.define(this));
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public <ORIGIN, TARGET, TRANSMUTER extends OTTransmuter<ORIGIN, TARGET>> TRANSMUTER get(Class<ORIGIN> origin, Class<TARGET> target) {
        String key = composeKey(origin, target);
        if (repository.containsKey(key)) {
            return (TRANSMUTER) repository.get(key);
        }
        return null;
    }

    @Override
    public <ORIGIN, TARGET> void remove(Class<ORIGIN> origin, Class<TARGET> target) {
        String key = composeKey(origin, target);
        repository.remove(key);
    }

    @Override
    public <ORIGIN, TARGET, TRANSMUTER extends OTTransmuter<ORIGIN, TARGET>> void remove(OTCustomTransmuterDefiner<ORIGIN, TARGET, TRANSMUTER> definer) {
        if (definer != null) {
            TRANSMUTER transmuter = definer.define(this);
            remove(transmuter.getOriginType(), transmuter.getTargetType());
        }
    }

    /**
     * @return key for types {@param origin} and {@param target}
     */
    private <ORIGIN, TARGET> String composeKey(Class<ORIGIN> origin, Class<TARGET> target) {
        return origin.getName() + "_" + target.getName();
    }

    /**
     * Check if transmuter is not null and is not already stored
     *
     * @param transmuter
     * @throws OTException
     */
    private <ORIGIN, TARGET, TRANSMUTER extends OTTransmuter<ORIGIN, TARGET>> void checkTransmuter(TRANSMUTER transmuter) throws OTException {
        if (transmuter == null) {
            throw new OTException("TRANSMUTER IS NULL");
        }

        Class<ORIGIN> origin = transmuter.getOriginType();
        Class<TARGET> target = transmuter.getTargetType();
        if (exists(origin, target)) {
            throw new OTException("TRANSMUTER ALREADY EXISTS FOR CLASSES: " + origin.getName() + " " + target.getName());
        }
    }

}
