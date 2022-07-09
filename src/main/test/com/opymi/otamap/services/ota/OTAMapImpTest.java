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
import beans.SimpleBean;
import beans.SpecularComplexBean;
import beans.SpecularSimpleBean;
import com.opymi.otamap.entry.OTMapper;
import com.opymi.otamap.entry.OTMapperBuilder;
import com.opymi.otamap.entry.OTRepository;
import com.opymi.otamap.entry.ServiceProvider;
import com.opymi.otamap.entry.services.JTypeEvaluator;
import com.opymi.otamap.entry.services.OTAMessageFormatter;
import com.opymi.otamap.entry.services.OTMapperBuilderProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test of {@link OTAMapImp}
 *
 * @author Antonino Verde
 * @since 2.0
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("ConstantConditions")
public class OTAMapImpTest {
    private final Class<ComplexBean> ORIGIN_TYPE = ComplexBean.class;
    private final Class<SpecularComplexBean> TARGET_TYPE = SpecularComplexBean.class;

    private OTAMapImp<ComplexBean, SpecularComplexBean> sut;
    @Mock private OTRepository repository;


    @Before
    public void setUp() throws Exception {
        sut = new OTAMapImp<>(repository, ORIGIN_TYPE, TARGET_TYPE);

        JTypeEvaluator jTypeEvaluator = ServiceProvider.getService(JTypeEvaluator.class);
        sut.setjTypeEvaluator(jTypeEvaluator);

        OTAMessageFormatter otaMessageFormatter = ServiceProvider.getService(OTAMessageFormatter.class);
        sut.setMessageFormatter(otaMessageFormatter);

        OTMapperBuilderProvider mapperBuilderProvider = ServiceProvider.getService(OTMapperBuilderProvider.class);
        sut.setMapperBuilderProvider(mapperBuilderProvider);

        OTMapperBuilderProvider otMapperBuilderProvider = ServiceProvider.getService(OTMapperBuilderProvider.class);
        OTMapperBuilder<ComplexBean, SpecularComplexBean> mapperBuilder = otMapperBuilderProvider.getBuilder(ORIGIN_TYPE, TARGET_TYPE);
        OTMapper<ComplexBean, SpecularComplexBean> mapper = createMapper(mapperBuilder);

        Mockito.doReturn(true).when(repository).exists(ORIGIN_TYPE, TARGET_TYPE);
        Mockito.doReturn(mapper).when(repository).get(ORIGIN_TYPE, TARGET_TYPE);
    }

    /**
     * Create mapper for {@link ComplexBean} and {@link SpecularComplexBean}
     *
     * @param mapperBuilder
     * @return mapper
     */
    private OTMapper<ComplexBean, SpecularComplexBean> createMapper(OTMapperBuilder<ComplexBean, SpecularComplexBean> mapperBuilder) {
        return mapperBuilder
                .customize(ComplexBean.Properties.A_CHAR, SpecularComplexBean.Properties.A_DIFFERENT_CHAR)
                .excludeField(ComplexBean.Properties.NO_READABLE)
                .excludeField(ComplexBean.Properties.NO_WRITABLE)
                .excludeField(ComplexBean.Properties.INTEGERS)
                .customize((complexBean, specularComplexBean) -> {
                    List<Integer> originIntegers = complexBean.getIntegers();
                    List<Integer> targetIntegers = new ArrayList<>(originIntegers);
                    specularComplexBean.setIntegers(targetIntegers);
                })
                .getMapper();
    }

    @Test
    public void mapOriginShallow() {
        SpecularComplexBean nullResult = sut.map(null);
        Assert.assertNull(nullResult);

        ComplexBean complexBean = buildDefaultComplexBean();
        SpecularComplexBean result = sut.map(complexBean);
        assertShallowMapping(complexBean, result);
    }

    @Test
    public void mapOriginDeep() {
        SpecularComplexBean nullResult = sut.map(null, true);
        Assert.assertNull(nullResult);

        ComplexBean complexBean = buildDefaultComplexBean();
        SpecularComplexBean result = sut.map(complexBean, true);
        assertDeepAutomatedMapping(complexBean, result);
    }

    @Test
    public void mapOriginTargetShallow() {
        SpecularComplexBean nullRsult = sut.map(null, new SpecularComplexBean());
        Assert.assertNull(nullRsult);

        ComplexBean complexBean = buildDefaultComplexBean();
        SpecularComplexBean result = sut.map(complexBean, new SpecularComplexBean());
        assertShallowMapping(complexBean, result);
    }

    @Test
    public void mapOriginTargetDeep() {
        SpecularComplexBean nullRsult = sut.map(null, new SpecularComplexBean(), true);
        Assert.assertNull(nullRsult);

        ComplexBean complexBean = buildDefaultComplexBean();
        SpecularComplexBean result = sut.map(complexBean, new SpecularComplexBean(), true);
        assertDeepAutomatedMapping(complexBean, result);
    }

    /**
     * Assert deep mapping of origin to target
     *
     * @param origin origin object
     * @param target target object
     */
    private void assertDeepAutomatedMapping(ComplexBean origin, SpecularComplexBean target) {
        assertShallowMapping(origin, target);

        SpecularSimpleBean targetSimpleBean = target.getSimpleBean();
        Assert.assertNotNull(targetSimpleBean);

        SimpleBean originSimpleBean = origin.getSimpleBean();
        Assert.assertEquals(originSimpleBean.getBigDecimalProp(), targetSimpleBean.getBigDecimalProp());
        Assert.assertEquals(originSimpleBean.getIntProp(), targetSimpleBean.getIntProp());
        Assert.assertEquals(originSimpleBean.getStringProp(), targetSimpleBean.getStringProp());
    }

    /**
     * Assert shallow mapping of origin to target
     *
     * @param origin origin object
     * @param target target object
     */
    private void assertShallowMapping(ComplexBean origin, SpecularComplexBean target) {
        Assert.assertEquals(origin.getaChar(), target.getaDifferentChar());
        Assert.assertEquals(origin.getaDouble(), (Double) target.getaDouble());
        Assert.assertEquals(origin.getIntegers(), target.getIntegers());
    }

    /**
     * @return a populated instance of {@link ComplexBean}
     */
    private ComplexBean buildDefaultComplexBean() {
        ComplexBean bean = new ComplexBean();
        bean.setaChar('a');
        bean.setaDouble(3D);

        SimpleBean simpleBean = new SimpleBean();
        simpleBean.setIntProp(1);
        simpleBean.setStringProp("STRING_PROP");
        simpleBean.setBigDecimalProp(BigDecimal.ONE);
        bean.setSimpleBean(simpleBean);

        List<Integer> integers = Arrays.asList(1, 2, 3);
        bean.setIntegers(integers);

        return bean;
    }

}
