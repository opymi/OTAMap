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

package com.opymi.otamap.resource.util;

import com.opymi.otamap.entry.OTAMap;
import com.opymi.otamap.entry.resource.OTAMessageFormatter;

import java.beans.PropertyDescriptor;

/**
 * Formatter {@link OTAMap} system messages
 *
 * @author Antonino Verde
 * @since 2.0
 */
public class OTAMessageFormatterImp implements OTAMessageFormatter {

    @Override
    public String format(PropertyDescriptor originProperty, PropertyDescriptor targetProperty, String detail) {
        String baseMessage = format(originProperty.getPropertyType(), targetProperty.getPropertyType(), detail);
        return "PROPERTIES " + originProperty.getName() + " -> " + targetProperty.getName() + " OF TYPES " + baseMessage;
    }

    @Override
    public String format(Class<?> origin, Class<?> target, String detail) {
        String baseMessage = createBaseMessage(origin, target);
        return String.format(baseMessage, detail);
    }

    private String createBaseMessage(Class<?> origin, Class<?> target) {
        return origin.getCanonicalName() + " -> " + target.getCanonicalName() + ": %s";
    }

}
