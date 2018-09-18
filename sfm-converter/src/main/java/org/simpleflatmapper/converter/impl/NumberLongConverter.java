package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

public class NumberLongConverter implements ContextualConverter<Number, Long> {
    @Override
    public Long convert(Number in, Context context) {
        if (in == null) return null;
        return in.longValue();
    }

    public String toString() {
        return "NumberToLong";
    }
}
