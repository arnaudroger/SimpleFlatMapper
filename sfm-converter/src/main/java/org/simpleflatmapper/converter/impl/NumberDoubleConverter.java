package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;

public class NumberDoubleConverter implements Converter<Number, Double> {
    @Override
    public Double convert(Number in, Context context) {
        if (in == null) return null;
        return in.doubleValue();
    }

    public String toString() {
        return "NumberToDouble";
    }
}
