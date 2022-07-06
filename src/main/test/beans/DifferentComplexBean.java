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

import java.util.List;

/**
 * Test Bean that has some slight variation from {@link ComplexBean}
 *
 * @author Antonino Verde
 * @since 2.0
 */
public class DifferentComplexBean {

    public static class Properties {
        public static final String A_DIFFERENT_SIMPLE_BEAN = "aDifferentSimpleBean";
        public static final String A_DIFFERENT_DOUBLE = "aDifferntDouble";
        public static final String A_DIFFERENT_CHAR = "aDifferentChar";
        public static final String DIFFERENT_INTEGERS = "differentIntegers";
    }

    private SimpleBean aDifferentSimpleBean;
    private Double aDifferntDouble;
    private char aDifferentChar;
    private List<Integer> differentIntegers;

    public SimpleBean getaDifferentSimpleBean() {
        return aDifferentSimpleBean;
    }

    public void setaDifferentSimpleBean(SimpleBean aDifferentSimpleBean) {
        this.aDifferentSimpleBean = aDifferentSimpleBean;
    }

    public Double getaDifferntDouble() {
        return aDifferntDouble;
    }

    public void setaDifferntDouble(Double aDifferntDouble) {
        this.aDifferntDouble = aDifferntDouble;
    }

    public char getaDifferentChar() {
        return aDifferentChar;
    }

    public void setaDifferentChar(char aDifferentChar) {
        this.aDifferentChar = aDifferentChar;
    }

    public List<Integer> getDifferentIntegers() {
        return differentIntegers;
    }

    public void setDifferentIntegers(List<Integer> differentIntegers) {
        this.differentIntegers = differentIntegers;
    }
}
