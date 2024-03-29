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

package com.opymi.otamap.services.mapper;

import com.opymi.otamap.entry.OTMapperBuilder;
import com.opymi.otamap.entry.OTOperativeMapper;
import com.opymi.otamap.entry.services.JTypeEvaluator;
import com.opymi.otamap.entry.services.OTMapperBuilderProvider;
import com.opymi.otamap.entry.services.TypeScanner;
import com.opymi.otamap.services.utils.JTypeEvaluatorImp;
import com.opymi.otamap.services.utils.TypeScannerImp;

import java.util.Objects;

/**
 * Provider of Builder {@link OTMapperBuilder} for mapper of {@param <ORIGIN>} to {@param <TARGET>}
 *
 * @author Antonino Verde
 * @since 2.0
 */
public class OTMapperBuilderProviderImp implements OTMapperBuilderProvider {

    @Override
    public <ORIGIN, TARGET> OTMapperBuilder<ORIGIN, TARGET> getBuilder(Class<ORIGIN> origin, Class<TARGET> target) {
        if (origin == null || target == null || Objects.equals(origin, target)) {
            throw new IllegalArgumentException("ORIGIN AND TARGET OBJECTS MUST BE NOT NULL AND NOT EQUALS");
        }

        JTypeEvaluator jTypeEvaluator = new JTypeEvaluatorImp();
        TypeScanner typeScanner = new TypeScannerImp();
        OTOperativeMapper<ORIGIN, TARGET> mapper = new OTMapperImp<>(typeScanner, jTypeEvaluator, origin, target);

        return new OTMapperBuilderImp<>(mapper);
    }

}
