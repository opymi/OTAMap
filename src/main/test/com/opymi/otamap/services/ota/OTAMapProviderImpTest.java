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

package com.opymi.otamap.services.ota;

import beans.ComplexBean;
import beans.SpecularComplexBean;
import com.opymi.otamap.entry.OTAMap;
import com.opymi.otamap.entry.OTRepository;
import com.opymi.otamap.entry.services.OTAMapProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Test of {@link OTAMapProviderImp}
 *
 * @author Antonino Verde
 * @since 2.0
 */
@RunWith(MockitoJUnitRunner.class)
public class OTAMapProviderImpTest {
    private final Class<ComplexBean> ORIGIN_TYPE = ComplexBean.class;
    private final Class<SpecularComplexBean> TARGET_TYPE = SpecularComplexBean.class;

    private OTAMapProvider sut;

    @Mock private OTRepository repository;

    @Before
    public void setUp() {
        sut = new OTAMapProviderImp();
    }

    @Test
    public void getOTAMapWithRepository() {
        OTAMap<ComplexBean, SpecularComplexBean> otaMap = sut.getOTAMap(repository, ORIGIN_TYPE, TARGET_TYPE);
        Assert.assertNotNull(otaMap);
    }

    @Test
    public void getOTAMapNoRepository() {
        OTAMap<ComplexBean, SpecularComplexBean> otaMap = sut.getOTAMap(ORIGIN_TYPE, TARGET_TYPE);
        Assert.assertNotNull(otaMap);
    }

}
