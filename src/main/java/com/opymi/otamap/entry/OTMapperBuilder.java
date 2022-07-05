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
 * Builder of mapper {@link OTMapper}
 *
 * @author Antonino Verde
 * @since 2.0
 */
public interface OTMapperBuilder<ORIGIN, TARGET> {

    /**
     * Customize the mapping with custom behavior
     * @param customMapperOperation implementation of {@link OTCustomMapperOperation} that defines custom operations
     * on fields
     *
     * @return current instance of {@link OTMapperBuilder}
     */
    OTMapperBuilder<ORIGIN, TARGET> customize(OTCustomMapperOperation<ORIGIN, TARGET> customMapperOperation);

    /**
     * Customize the mapping for fields with different names
     * @param originField origin's field name
     * @param targetField target's field name
     *
     * @return current instance of {@link OTMapperBuilder}
     */
    OTMapperBuilder<ORIGIN, TARGET> customize(String originField, String targetField);

    /**
     * Exclude target's field from the mapping
     * @param field target's field name
     * @param force if true doesn't check if the field exists
     *
     * @return current instance of {@link OTMapperBuilder}
     */
    OTMapperBuilder<ORIGIN, TARGET> excludeField(String field, boolean force);

    /**
     * Exclude field from mapping
     * @param field
     * @return current instance of {@link OTMapperBuilder}
     */
    OTMapperBuilder<ORIGIN, TARGET> excludeField(String field);

    /**
     * Exclude all fields from mapping
     * @return current instance of {@link OTMapperBuilder}
     */
    OTMapperBuilder<ORIGIN, TARGET> excludeAllFields();

    /**
     * @return builded mapper {@link OTMapper}
     */
    OTMapper<ORIGIN, TARGET> getMapper();

}
