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

package com.opymi.otamap.entry.services;

import com.opymi.otamap.annotations.OTAService;
import com.opymi.otamap.entry.OTAMap;
import com.opymi.otamap.entry.OTRepository;

/**
 * {@link OTAMap} Factory
 *
 * @author Antonino Verde
 * @since 2.0
 */
@OTAService
public interface OTAMapProvider {

    /**
     * Create an {@link OTAMap} instance from origin's type and target's type
     *
     * @param repository
     * @param origin
     * @param target
     *
     * @param <ORIGIN> origin type
     * @param <TARGET> target type
     * @return {@link OTAMap} instance
     */
    <ORIGIN, TARGET> OTAMap<ORIGIN, TARGET> getOTAMap(OTRepository repository, Class<ORIGIN> origin, Class<TARGET> target);

    /**
     * @see #getOTAMap(OTRepository, Class, Class)
     */
    <ORIGIN, TARGET> OTAMap<ORIGIN, TARGET> getOTAMap(Class<ORIGIN> origin, Class<TARGET> target);

}
