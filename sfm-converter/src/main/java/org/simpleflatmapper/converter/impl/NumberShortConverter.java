package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;

public class NumberShortConverter implements Converter<Number, Short> {
    @Override
    public Short convert(Number in, Context context) {
        if (in == null) return null;
        return in.shortValue();
    }

    public String toString() {
        return "NumberToShort";
    }
}
