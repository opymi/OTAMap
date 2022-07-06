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

import java.math.BigInteger;
import java.util.List;

/**
 * Test Bean specular to {@link ComplexBean}
 *
 * @author Antonino Verde
 * @since 2.0
 */
public class SpecularComplexBean {

    public static class Properties {
        public static final String SIMPLE_BEAN = "simpleBean";
        public static final String A_DOUBLE = "aDouble";
        public static final String A_DIFFERENT_CHAR = "aDifferentChar";
        public static final String INTEGERS = "integers";
        public static final String NO_READABLE = "noReadable";
        public static final String NO_WRITABLE = "noWritable";
    }

    private SpecularSimpleBean simpleBean;
    private double aDouble;
    private char aDifferentChar;
    private List<Integer> integers;
    private BigInteger noReadable;
    private int noWritable = 1;

    public SpecularSimpleBean getSimpleBean() {
        return simpleBean;
    }

    public void setSimpleBean(SpecularSimpleBean simpleBean) {
        this.simpleBean = simpleBean;
    }

    public double getaDouble() {
        return aDouble;
    }

    public void setaDouble(double aDouble) {
        this.aDouble = aDouble;
    }

    public char getaDifferentChar() {
        return aDifferentChar;
    }

    public void setaDifferentChar(char aDifferentChar) {
        this.aDifferentChar = aDifferentChar;
    }

    public List<Integer> getIntegers() {
        return integers;
    }

    public void setIntegers(List<Integer> integers) {
        this.integers = integers;
    }

    public void setNoReadable(BigInteger noReadable) {
        this.noReadable = noReadable;
    }

    public int getNoWritable() {
        return noWritable;
    }

}
