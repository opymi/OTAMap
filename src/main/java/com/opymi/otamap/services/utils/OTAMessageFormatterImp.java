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

package com.opymi.otamap.services.utils;

import com.opymi.otamap.entry.OTAMap;
import com.opymi.otamap.entry.services.OTAMessageFormatter;

import java.beans.PropertyDescriptor;

/**
 * Formatter {@link OTAMap} system messages
 *
 * @author Antonino Verde
 * @since 2.0
 */
public class OTAMessageFormatterImp implements OTAMessageFormatter {
    public static final String MAPPING_MESSAGE_FORMAT = "%s -> %s";
    public static final String PROPERTIES_MAPPING_MESSAGE_FORMAT = "%s -> %s OF TYPES " + MAPPING_MESSAGE_FORMAT;
    public static final String DETAIL_FORMAT = ": %s";

    @Override
    public String formatMappingMessage(PropertyDescriptor originProperty, PropertyDescriptor targetProperty, String detail) {
        String message = String.format(PROPERTIES_MAPPING_MESSAGE_FORMAT, originProperty.getName(), targetProperty.getName(), originProperty.getPropertyType(), targetProperty.getPropertyType());
        return message + formatDetail(detail);
    }

    @Override
    public String formatMappingMessage(Class<?> origin, Class<?> target, String detail) {
        String message = String.format(MAPPING_MESSAGE_FORMAT, origin.getCanonicalName(), target.getCanonicalName());
        return message + formatDetail(detail);
    }

    private String formatDetail(String detail) {
        return String.format(DETAIL_FORMAT, detail);
    }

}
