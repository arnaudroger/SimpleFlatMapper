package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

public class NumberFloatConverter implements ContextualConverter<Number, Float> {
    @Override
    public Float convert(Number in, Context context) {
        if (in == null) return null;
        return in.floatValue();
    }

    public String toString() {
        return "NumberToFloat";
    }
}
