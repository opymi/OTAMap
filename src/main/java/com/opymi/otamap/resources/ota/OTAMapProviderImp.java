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

package com.opymi.otamap.resources.ota;

import com.opymi.otamap.entry.OTAMap;
import com.opymi.otamap.entry.OTRepository;
import com.opymi.otamap.entry.resources.JTypeEvaluator;
import com.opymi.otamap.entry.resources.OTAMapProvider;
import com.opymi.otamap.entry.resources.OTAMessageFormatter;
import com.opymi.otamap.exceptions.OTException;
import com.opymi.otamap.resources.repository.OTRepositoryImp;

/**
 * {@link OTAMap} Factory
 *
 * @author Antonino Verde
 * @since 2.0
 */
public class OTAMapProviderImp implements OTAMapProvider {
    private final JTypeEvaluator jTypeEvaluator;
    private final OTAMessageFormatter messageFormatter;


    public OTAMapProviderImp(JTypeEvaluator jTypeEvaluator, OTAMessageFormatter messageFormatter) {
        this.jTypeEvaluator = jTypeEvaluator;
        this.messageFormatter = messageFormatter;
    }

    @Override
    public <ORIGIN, TARGET> OTAMap<ORIGIN, TARGET> getOTAMap(Class<ORIGIN> origin, Class<TARGET> target) {
        OTRepository repository = new OTRepositoryImp();
        return getOTAMap(repository, origin, target);
    }

    @Override
    public <ORIGIN, TARGET> OTAMap<ORIGIN, TARGET> getOTAMap(OTRepository repository, Class<ORIGIN> origin, Class<TARGET> target) {
        if (origin == null || target == null) {
            throw new OTException("TYPES MANDATORY");
        }
        return createOTAMapImp(repository, origin, target);
    }

    /**
     * Create and initialize specific implementation {@link OTAMapImp} of {@link OTAMap}
     *
     * @param repository
     * @param origin
     * @param target
     *
     * @return {@link OTAMap} implementation
     *
     * @param <ORIGIN>
     * @param <TARGET>
     */
    private <ORIGIN, TARGET> OTAMap<ORIGIN, TARGET> createOTAMapImp(OTRepository repository, Class<ORIGIN> origin, Class<TARGET> target) {
        OTAMapImp<ORIGIN, TARGET> otaMap = new OTAMapImp<>(repository, origin, target);
        otaMap.setjTypeEvaluator(jTypeEvaluator);
        otaMap.setMessageFormatter(messageFormatter);
        return otaMap;
    }

}
