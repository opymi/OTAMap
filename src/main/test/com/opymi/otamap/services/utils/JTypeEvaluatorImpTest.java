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

import com.opymi.otamap.entry.services.JTypeEvaluator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

/**
 * Test of {@link JTypeEvaluatorImp}
 *
 * @author Antonino Verde
 * @since 2.0
 */
public class JTypeEvaluatorImpTest {

    private JTypeEvaluator sut;

    @Before
    public void setUp() {
        sut = new JTypeEvaluatorImp();
    }

    @Test
    public void isPrimitivable() {
        assertPrimitivable(Boolean.class, boolean.class);
        assertPrimitivable(Integer.class, int.class);
        assertPrimitivable(Double.class, double.class);
        assertPrimitivable(Float.class, float.class);
        assertPrimitivable(Long.class, long.class);
        assertPrimitivable(Short.class, short.class);
        assertPrimitivable(Character.class, char.class);
        assertPrimitivable(Byte.class, byte.class);

        assertNotPrimitivable(Boolean.class, byte.class);
        assertNotPrimitivable(BigDecimal.class, double.class);
        assertNotPrimitivable(Integer.class, long.class);
    }

    private void assertPrimitivable(Class<?> type1, Class<?> type2) {
        Assert.assertTrue(sut.isPrimitivable(type1, type2));
        Assert.assertTrue(sut.isPrimitivable(type2, type1));
    }

    private void assertNotPrimitivable(Class<?> type1, Class<?> type2) {
        Assert.assertFalse(sut.isPrimitivable(type1, type2));
        Assert.assertFalse(sut.isPrimitivable(type2, type1));
    }

    @Test
    public void isUnsupportedType() {
        Assert.assertTrue(sut.isUnsupportedType(Collection.class));
        Assert.assertTrue(sut.isUnsupportedType(List.class));
        Assert.assertTrue(sut.isUnsupportedType(ArrayList.class));
        Assert.assertTrue(sut.isUnsupportedType(Set.class));
        Assert.assertTrue(sut.isUnsupportedType(HashSet.class));
        Assert.assertTrue(sut.isUnsupportedType(Map.class));
        Assert.assertTrue(sut.isUnsupportedType(String[].class));
        Assert.assertTrue(sut.isUnsupportedType(Integer[].class));

        Assert.assertFalse(sut.isUnsupportedType(Integer.class));
        Assert.assertFalse(sut.isUnsupportedType(BigDecimal.class));
    }

}
