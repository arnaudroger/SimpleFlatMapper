package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;

public class CharSequenceByteConverter implements Converter<CharSequence, Byte> {
    @Override
    public Byte convert(CharSequence in, Context context) throws Exception {
        if (in == null) return null;
        return Byte.valueOf(in.toString());
    }

    public String toString() {
        return "CharSequenceToByte";
    }
}
