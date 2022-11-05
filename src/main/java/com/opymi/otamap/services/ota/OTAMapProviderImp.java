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

package com.opymi.otamap.services.ota;

import com.opymi.otamap.entry.OTAMap;
import com.opymi.otamap.entry.OTRepository;
import com.opymi.otamap.entry.ServiceProvider;
import com.opymi.otamap.entry.services.JTypeEvaluator;
import com.opymi.otamap.entry.services.OTAMapProvider;
import com.opymi.otamap.entry.services.OTAMessageFormatter;
import com.opymi.otamap.entry.services.OTMapperBuilderProvider;
import com.opymi.otamap.exceptions.OTException;
import com.opymi.otamap.services.repository.OTRepositoryImp;
import com.opymi.otamap.services.utils.JTypeEvaluatorImp;
import com.opymi.otamap.services.utils.OTAMessageFormatterImp;

/**
 * {@link OTAMap} Factory
 *
 * @author Antonino Verde
 * @since 2.0
 */
public class OTAMapProviderImp implements OTAMapProvider {

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

        JTypeEvaluator jTypeEvaluator = new JTypeEvaluatorImp();
        otaMap.setjTypeEvaluator(jTypeEvaluator);

        OTAMessageFormatter messageFormatter = new OTAMessageFormatterImp();
        otaMap.setMessageFormatter(messageFormatter);

        OTMapperBuilderProvider mapperBuilderProvider = ServiceProvider.getService(OTMapperBuilderProvider.class);
        otaMap.setMapperBuilderProvider(mapperBuilderProvider);

        return otaMap;
    }

}
