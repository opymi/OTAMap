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

import beans.SimpleBean;
import beans.SubSimpleBean;
import com.opymi.otamap.entry.services.OTAMessageFormatter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.beans.PropertyDescriptor;

/**
 * Test of {@link OTAMessageFormatterImp}
 *
 * @author Antonino Verde
 * @since 2.0
 */
@RunWith(MockitoJUnitRunner.class)
public class OTAMessageFormatterImpTest {
    private final Class<?> ORIGIN_TYPE = SimpleBean.class;
    private final Class<?> TARGET_TYPE = SubSimpleBean.class;
    private final String DETAIL_MESSAGE = "DETAIL";

    private OTAMessageFormatter sut;

    @Mock private PropertyDescriptor originProperty;
    @Mock private PropertyDescriptor targetProperty;

    @Before
    public void setUp() {
        sut = new OTAMessageFormatterImp();

        Mockito.doReturn("ORIGIN_PROPERTY_NAME").when(originProperty).getName();
        Mockito.doReturn(ORIGIN_TYPE).when(originProperty).getPropertyType();

        Mockito.doReturn("TARGET_PROPERTY_NAME").when(targetProperty).getName();
        Mockito.doReturn(TARGET_TYPE).when(targetProperty).getPropertyType();
    }

    @Test
    public void formatMappingMessageClassParameters() {
        String mappingMessage = String.format(OTAMessageFormatterImp.MAPPING_MESSAGE_FORMAT, ORIGIN_TYPE.getName(), TARGET_TYPE.getName());
        String detailMessage = String.format(OTAMessageFormatterImp.DETAIL_FORMAT, DETAIL_MESSAGE);
        String expectedMessage = mappingMessage + detailMessage;

        String actualMessage = sut.formatMappingMessage(ORIGIN_TYPE, TARGET_TYPE, DETAIL_MESSAGE);
        Assert.assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void formatMappingMessagePropertyParameter() {
        String mappingMessage = String.format(OTAMessageFormatterImp.PROPERTIES_MAPPING_MESSAGE_FORMAT, originProperty.getName(), targetProperty.getName(), originProperty.getPropertyType(), targetProperty.getPropertyType());
        String detailMessage = String.format(OTAMessageFormatterImp.DETAIL_FORMAT, DETAIL_MESSAGE);
        String expectedMessage = mappingMessage + detailMessage;

        String actualMessage = sut.formatMappingMessage(originProperty, targetProperty, DETAIL_MESSAGE);
        Assert.assertEquals(expectedMessage, actualMessage);
    }

}
