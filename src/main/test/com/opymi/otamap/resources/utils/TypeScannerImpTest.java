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

package com.opymi.otamap.resources.utils;

import beans.SimpleBean;
import beans.SubSimpleBean;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Test of {@link TypeScannerImp}
 *
 * @author Antonino Verde
 * @since 2.0
 */
public class TypeScannerImpTest {
    private final Class<?> BEAN_TYPE = SubSimpleBean.class;
    private final int EXPECTED_PROPERTIES = 4;

    private TypeScannerImp sut;

    @Before
    public void setUp() {
        sut = new TypeScannerImp();
    }

    @Test
    public void retrievePropertyDescriptors() {
        List<PropertyDescriptor> propertyDescriptors = sut.retrievePropertyDescriptors(BEAN_TYPE);
        Assert.assertEquals(EXPECTED_PROPERTIES, propertyDescriptors.size());

        List<String> properties = propertyDescriptors.stream().map(PropertyDescriptor::getName).collect(Collectors.toList());
        assertProperties(properties);
    }

    @Test
    public void retrieveDeclaredFieldsNames() {
        Set<String> declaredFieldsNames = sut.retrieveDeclaredFieldsNames(BEAN_TYPE);
        Assert.assertEquals(EXPECTED_PROPERTIES, declaredFieldsNames.size());
        assertProperties(declaredFieldsNames);
    }

    @Test
    public void retrieveDeclaredFields() {
        Set<Field> declaredFields = sut.retrieveDeclaredFields(BEAN_TYPE);
        Assert.assertEquals(EXPECTED_PROPERTIES, declaredFields.size());

        List<String> properties = declaredFields.stream().map(Field::getName).collect(Collectors.toList());
        assertProperties(properties);
    }

    private void assertProperties(Collection<String> properties) {
        Assert.assertTrue(properties.contains(SimpleBean.Properties.STRING_PROP));
        Assert.assertTrue(properties.contains(SimpleBean.Properties.INT_PROP));
        Assert.assertTrue(properties.contains(SimpleBean.Properties.BIGDECIMAL_PROP));
        Assert.assertTrue(properties.contains(SubSimpleBean.Properties.BOOLEAN_PROP));
    }

}
