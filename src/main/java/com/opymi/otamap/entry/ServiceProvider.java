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

import com.opymi.otamap.annotations.OTAService;
import com.opymi.otamap.entry.services.*;
import com.opymi.otamap.exceptions.OTException;
import com.opymi.otamap.services.converter.OTConverterProviderImp;
import com.opymi.otamap.services.mapper.OTMapperBuilderProviderImp;
import com.opymi.otamap.services.ota.OTAMapProviderImp;
import com.opymi.otamap.services.utils.JTypeEvaluatorImp;
import com.opymi.otamap.services.utils.OTAMessageFormatterImp;
import com.opymi.otamap.services.utils.TypeScannerImp;

/**
 * Provider of available library resources {@link OTAService}
 *
 * @author Antonino Verde
 * @since 2.0
 */
public class ServiceProvider {

    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> service) {
        if(service.getAnnotation(OTAService.class) != null) {
            if(OTAMapProvider.class.equals(service)) {
                return (T) new OTAMapProviderImp();
            }
            else if(OTMapperBuilderProvider.class.equals(service)) {
                return (T) new OTMapperBuilderProviderImp();
            }
            else if(OTConverterProvider.class.equals(service)) {
                return (T) new OTConverterProviderImp();
            }
            else if(TypeScanner.class.equals(service)) {
                return (T) new TypeScannerImp();
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
