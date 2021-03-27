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
