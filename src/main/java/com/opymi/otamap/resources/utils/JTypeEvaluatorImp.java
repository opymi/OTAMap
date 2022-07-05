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

import com.opymi.otamap.entry.resources.JTypeEvaluator;

import java.util.*;

/**
 * Utility to evaluate java base type
 *
 * @author Antonino Verde
 * @since 2.0
 */
public class JTypeEvaluatorImp implements JTypeEvaluator {
    private final Map<Class<?>, Class<?>> SIMPLE_WRAPPER_TYPES;
    private final List<Class<?>> UNSUPPORTED_TYPES;

    public JTypeEvaluatorImp() {
        SIMPLE_WRAPPER_TYPES = new HashMap<>();
        SIMPLE_WRAPPER_TYPES.put(Boolean.class, boolean.class);
        SIMPLE_WRAPPER_TYPES.put(Integer.class, int.class);
        SIMPLE_WRAPPER_TYPES.put(Double.class, double.class);
        SIMPLE_WRAPPER_TYPES.put(Float.class, float.class);
        SIMPLE_WRAPPER_TYPES.put(Long.class, long.class);
        SIMPLE_WRAPPER_TYPES.put(Short.class, short.class);
        SIMPLE_WRAPPER_TYPES.put(Character.class, char.class);
        SIMPLE_WRAPPER_TYPES.put(Byte.class, byte.class);

        UNSUPPORTED_TYPES = new ArrayList<>();
        UNSUPPORTED_TYPES.add(Collection.class);
        UNSUPPORTED_TYPES.add(Map.class);
    }


    @Override
    public boolean isPrimitivable(Class<?> origin, Class<?> target) {
        return (origin.isPrimitive() && Objects.equals(origin, SIMPLE_WRAPPER_TYPES.get(target))) || (target.isPrimitive() && Objects.equals(target, SIMPLE_WRAPPER_TYPES.get(origin)));
    }

    @Override
    public boolean isUnsupportedType(Class<?> type) {
        return type.isArray() || UNSUPPORTED_TYPES.stream().anyMatch(unsupported -> unsupported.isAssignableFrom(type));
    }

}
