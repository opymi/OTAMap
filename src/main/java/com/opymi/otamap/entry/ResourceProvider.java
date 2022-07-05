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

import com.opymi.otamap.annotation.OTAResource;
import com.opymi.otamap.entry.resource.JTypeEvaluator;
import com.opymi.otamap.entry.resource.OTAMapProvider;
import com.opymi.otamap.entry.resource.OTAMessageFormatter;
import com.opymi.otamap.entry.resource.OTMapperBuilderProvider;
import com.opymi.otamap.exception.OTException;
import com.opymi.otamap.entry.resource.OTConverterProvider;
import com.opymi.otamap.resource.converter.OTConverterProviderImp;
import com.opymi.otamap.resource.mapper.OTMapperBuilderProviderImp;
import com.opymi.otamap.resource.ota.OTAMapProviderImp;
import com.opymi.otamap.resource.util.JTypeEvaluatorImp;
import com.opymi.otamap.resource.util.OTAMessageFormatterImp;

/**
 * Provider of available library resources {@link OTAResource}
 *
 * @author Antonino Verde
 * @since 2.0
 */
public class ResourceProvider {

    @SuppressWarnings("unchecked")
    public static <T> T getResource(Class<T> service) {
        if(service.getAnnotation(OTAResource.class) != null) {
            if(OTAMapProvider.class.equals(service)) {
                JTypeEvaluator jTypeEvaluator = getResource(JTypeEvaluator.class);
                OTAMessageFormatter messageFormatter = getResource(OTAMessageFormatter.class);
                return (T) new OTAMapProviderImp(jTypeEvaluator, messageFormatter);
            }
            else if(OTMapperBuilderProvider.class.equals(service)) {
                JTypeEvaluator jTypeEvaluator = getResource(JTypeEvaluator.class);
                return (T) new OTMapperBuilderProviderImp(jTypeEvaluator);
            }
            else if(OTConverterProvider.class.equals(service)) {
                return (T) new OTConverterProviderImp();
            }
            else if(JTypeEvaluator.class.equals(service)) {
                return (T) new JTypeEvaluatorImp();
            }
            else if(OTAMessageFormatter.class.equals(service)) {
                return (T) new OTAMessageFormatterImp();
            }
        }
        throw new OTException(service.getName() + " UNMANAGED RESOURCE");
    }

}
