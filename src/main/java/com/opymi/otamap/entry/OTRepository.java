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

package com.opymi.otamap.entry;

/**
 * Repository of {@link OTTransmuter} defined by user
 *
 * @author Antonino Verde
 * @since 2.0
 */
public interface OTRepository {

    /**
     * @param origin origin's type
     * @param target target's type
     *
     * @return true if exists transmuter for types
     */
    <ORIGIN, TARGET> boolean exists(Class<ORIGIN> origin, Class<TARGET> target);

    /**
     * store {@param transmuter}
     */
    <ORIGIN, TARGET> void store(OTTransmuter<ORIGIN, TARGET> transmuter);

    /**
     * store transmuter by {@param definer}
     */
    <ORIGIN, TARGET> void store(OTCustomTransmuterDefiner<ORIGIN, TARGET> definer);

    /**
     * @param origin origin's type
     * @param target target's type
     *
     * @return transmuter for types
     */
    <ORIGIN, TARGET> OTTransmuter<ORIGIN, TARGET> get(Class<ORIGIN> origin, Class<TARGET> target);

    /**
     * remove transmuter by types
     */
    <ORIGIN, TARGET> void remove(Class<ORIGIN> origin, Class<TARGET> target);

    /**
     * remove transmuter by {@param definer}
     */
    <ORIGIN, TARGET> void remove(OTCustomTransmuterDefiner<ORIGIN, TARGET> definer);

}
