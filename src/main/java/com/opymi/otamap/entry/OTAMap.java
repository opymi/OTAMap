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

import com.opymi.otamap.exception.AccessPropertyException;
import com.opymi.otamap.exception.CreateInstanceException;

/**
 * Core engine to build a {@param <TARGET>} object from an {@param <ORIGIN>} object
 *
 * @param <ORIGIN>
 * @param <TARGET>
 *
 * @author Antonino Verde
 * @since 2.0
 */
public interface OTAMap<ORIGIN, TARGET> {

    /**
     * Build the target's object from origin's object.
     *
     * @param origin origin object
     * @param target target object
     * @param deepAutomatedMapping mapping mode
     * @return builded target object
     *
     * @throws AccessPropertyException
     * @throws CreateInstanceException
     */
    TARGET map(ORIGIN origin, TARGET target, boolean deepAutomatedMapping);

    /**
     * @see OTAMap#map(ORIGIN, TARGET, boolean)
     */
    TARGET map(ORIGIN origin, boolean deepAutomatedMapping);

    /**
     * @see OTAMap#map(ORIGIN, TARGET, boolean)
     */
    TARGET map(ORIGIN origin, TARGET target);

    /**
     * @see OTAMap#map(ORIGIN, TARGET, boolean)
     */
    TARGET map(ORIGIN origin);

}