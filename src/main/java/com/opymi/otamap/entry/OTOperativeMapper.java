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

/**
 * Mapper of the {@param <ORIGIN>} to {@param <TARGET>}
 *
 * @author Antonino Verde
 * @since 2.0
 */
public interface OTOperativeMapper<ORIGIN, TARGET> extends OTMapper<ORIGIN, TARGET> {

    /**
     * Add the custom behavior
     * @param customMapperOperation implementation of {@link OTCustomMapperOperation} that defines custom operations
     * on fields
     */
    void setCustomOperation(OTCustomMapperOperation<ORIGIN, TARGET> customMapperOperation);

    /**
     * Add custom mapping for fields with different names
     * @param originField origin's field name
     * @param targetField target's field name
     */
    void addCutomNameMapping(String originField, String targetField);

    /**
     * Exclude field from the mapping
     * @param field field's name
     */
    void excludeField(String field, boolean force);

    /**
     * Exclude field from the mapping
     */
    void excludeAllFields();

}
