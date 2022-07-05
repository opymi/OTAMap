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

package com.opymi.otamap.resource.mapper;


import com.opymi.otamap.entry.OTCustomMapperOperation;
import com.opymi.otamap.entry.OTMapper;
import com.opymi.otamap.entry.OTMapperBuilder;
import com.opymi.otamap.entry.OTOperativeMapper;

/**
 * Builder of mapper {@link OTMapper}
 *
 * @author Antonino Verde
 * @since 2.0
 */
public class OTMapperBuilderImp<ORIGIN, TARGET> implements OTMapperBuilder<ORIGIN, TARGET> {
    private final OTOperativeMapper<ORIGIN, TARGET> mapper;

    public OTMapperBuilderImp(OTOperativeMapper<ORIGIN, TARGET> mapper) {
        this.mapper = mapper;
    }

    @Override
    public OTMapperBuilder<ORIGIN, TARGET> customize(OTCustomMapperOperation<ORIGIN, TARGET> customMapperOperation) {
        mapper.setCustomOperation(customMapperOperation);
        return this;
    }

    @Override
    public OTMapperBuilder<ORIGIN, TARGET> customize(String originField, String targetField) {
        mapper.addCutomNameMapping(originField, targetField);
        return this;
    }

    @Override
    public OTMapperBuilder<ORIGIN, TARGET> excludeField(String field, boolean force) {
        mapper.excludeField(field, force);
        return this;
    }

    @Override
    public OTMapperBuilder<ORIGIN, TARGET> excludeField(String field) {
        excludeField(field, false);
        return this;
    }

    @Override
    public OTMapperBuilder<ORIGIN, TARGET> excludeAllFields() {
        mapper.excludeAllFields();
        return this;
    }

    @Override
    public OTMapper<ORIGIN, TARGET> getMapper() {
        return mapper;
    }

}
