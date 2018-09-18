package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

public class NumberIntegerConverter implements ContextualConverter<Number, Integer> {
    @Override
    public Integer convert(Number in, Context context) {
        if (in == null) return null;
        return in.intValue();
    }

    public String toString() {
        return "NumberToInteger";
    }
}
