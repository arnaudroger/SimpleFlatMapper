package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;

public class CharSequenceFloatConverter implements Converter<CharSequence, Float> {
    @Override
    public Float convert(CharSequence in, Context context) throws Exception {
        if (in == null) return null;
        return Float.valueOf(in.toString());
    }

    public String toString() {
        return "CharSequenceToFloat";
    }
}
