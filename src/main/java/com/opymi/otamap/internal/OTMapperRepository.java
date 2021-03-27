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
