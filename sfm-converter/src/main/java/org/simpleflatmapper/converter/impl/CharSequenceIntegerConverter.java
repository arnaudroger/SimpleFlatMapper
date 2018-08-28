package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;

public class CharSequenceIntegerConverter implements Converter<CharSequence, Integer> {
    @Override
    public Integer convert(CharSequence in, Context context) throws Exception {
        if (in == null) return null;
        return Integer.valueOf(in.toString());
    }

    public String toString() {
        return "CharSequenceToInteger";
    }
}
