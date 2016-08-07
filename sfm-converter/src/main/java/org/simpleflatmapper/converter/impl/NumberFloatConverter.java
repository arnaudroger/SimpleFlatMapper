package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Converter;

public class NumberFloatConverter implements Converter<Number, Float> {
    @Override
    public Float convert(Number in) {
        if (in == null) return null;
        return in.floatValue();
    }

    public String toString() {
        return "NumberToFloat";
    }
}
