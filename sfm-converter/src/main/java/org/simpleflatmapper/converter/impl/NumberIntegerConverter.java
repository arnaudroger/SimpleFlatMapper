package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Converter;

public class NumberIntegerConverter implements Converter<Number, Integer> {
    @Override
    public Integer convert(Number in) {
        if (in == null) return null;
        return in.intValue();
    }

    public String toString() {
        return "NumberToInteger";
    }
}
