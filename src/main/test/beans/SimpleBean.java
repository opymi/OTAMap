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

package beans;

import java.math.BigDecimal;

/**
 * Test Bean
 *
 * @author Antonino Verde
 * @since 2.0
 */
public class SimpleBean {

    public static class Properties {
        public static final String STRING_PROP = "stringProp";
        public static final String INT_PROP = "intProp";
        public static final String BIGDECIMAL_PROP = "bigDecimalProp";
    }

    private String stringProp;
    private int intProp;
    private BigDecimal bigDecimalProp;

    public String getStringProp() {
        return stringProp;
    }

    public void setStringProp(String stringProp) {
        this.stringProp = stringProp;
    }

    public int getIntProp() {
        return intProp;
    }

    public void setIntProp(int intProp) {
        this.intProp = intProp;
    }

    public BigDecimal getBigDecimalProp() {
        return bigDecimalProp;
    }

    public void setBigDecimalProp(BigDecimal bigDecimalProp) {
        this.bigDecimalProp = bigDecimalProp;
    }
}
