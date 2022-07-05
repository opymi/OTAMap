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

import com.opymi.otamap.resource.repository.OTRepositoryImp;

/**
 * Interface that needs to be implemented by classes that defines custom transmuter
 * @param <ORIGIN> origin type
 * @param <TARGET> target type
 *
 * @author Antonino Verde
 * @since 1.0
 */
public interface OTCustomTransmuterDefiner<ORIGIN, TARGET, TRANSMUTER extends OTTransmuter<ORIGIN, TARGET>> {

    /**
     * @param repository of transmuters
     * @return transmuter  of origin's type to target's type
     */
    <REPO extends OTRepositoryImp> TRANSMUTER define(REPO repository);

}
