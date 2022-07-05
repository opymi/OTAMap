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

package com.opymi.otamap.resource.converter;

import com.opymi.otamap.exception.OTException;
import com.opymi.otamap.entry.OTConverter;

import java.util.function.Function;

/**
 * Class that builds the converter of the {@param <ORIGIN>} to {@param <TARGET>}
 *
 * @author Antonino Verde
 * @since 1.0
 */
public class OTConverterBuilder<ORIGIN, TARGET> {
    private final OTConverterImpl<ORIGIN, TARGET> otConverter;

    private OTConverterBuilder(Class<ORIGIN> originType, Class<TARGET> targetType) {
        this.otConverter = new OTConverterImpl<>(originType, targetType);
    }

    /**
     * Create an {@link OTConverterBuilder} instance from origin's type and target's type
     *
     * @param originType origin's type
     * @param targetType target's type
     * @return {@link OTConverterBuilder} instance
     */
    public static <ORIGIN, TARGET> OTConverterBuilder<ORIGIN, TARGET> instance(Class<ORIGIN> originType, Class<TARGET> targetType) {
        return new OTConverterBuilder<>(originType, targetType);
    }

    /**
     * @param converter function that defines the operations to convert origin to target
     * @return {@link OTConverter}
     */
    public final OTConverter<ORIGIN, TARGET> build(Function<ORIGIN, TARGET> converter) {
        otConverter.setConverter(converter);
        return otConverter;
    }

    /**
     * Implemetation of {@link OTConverter}
     * @param <ORIGIN>
     * @param <TARGET>
     */
    private static class OTConverterImpl<ORIGIN, TARGET> implements OTConverter<ORIGIN, TARGET> {
        private final Class<ORIGIN> originType;
        private final Class<TARGET> targetType;
        private Function<ORIGIN, TARGET> converter;

        private OTConverterImpl(Class<ORIGIN> originType, Class<TARGET> targetType) {
            this.originType = originType;
            this.targetType = targetType;
        }

        @Override
        public Class<ORIGIN> getOriginType() {
            return originType;
        }

        @Override
        public Class<TARGET> getTargetType() {
            return targetType;
        }

        @Override
        public TARGET convert(ORIGIN origin) {
            if (converter == null) {
                throw new OTException("CONVERTER NOT DEFINED");
            }
            return origin != null ? converter.apply(origin) : null;
        }

        private void setConverter(Function<ORIGIN, TARGET> converter) {
            this.converter = converter;
        }
    }
}
