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

package com.opymi.otamap.resource.mapper;

import com.opymi.otamap.entry.OTMapper;
import com.opymi.otamap.entry.OTRepository;

/**
 * Factory of {@link OTMapper}
 *
 * @author Antonino Verde
 * @since 1.0
 */
public class OTMapperFactory {

    private final OTRepository repository;

    public OTMapperFactory(OTRepository repository) {
        this.repository = repository;
    }

    public OTMapperFactory() {
        this(null);
    }

    /**
     * Check if mapper defined for types exists and return it or default mapper
     *
     * @param origin origin type
     * @param target target type
     * @return instance of {@link OTMapper} for types {@param origin} and {@param target}
     */
    public <ORIGIN, TARGET> OTMapper<ORIGIN, TARGET> mapperForClasses(Class<ORIGIN> origin, Class<TARGET> target) {
        if (repository != null && repository.exists(origin, target)) {
            return repository.get(origin, target);
        }
        return OTMapperBuilder.instance(origin, target).build();
    }

}
