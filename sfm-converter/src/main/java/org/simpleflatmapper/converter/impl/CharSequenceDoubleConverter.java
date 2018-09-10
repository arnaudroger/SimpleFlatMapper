package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;

public class CharSequenceDoubleConverter implements Converter<CharSequence, Double> {
    @Override
    public Double convert(CharSequence in, Context context) throws Exception {
        if (in == null) return null;
        return Double.valueOf(in.toString());
    }

    public String toString() {
        return "CharSequenceToDouble";
    }
}
