package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

public class NumberByteConverter implements ContextualConverter<Number, Byte> {
    @Override
    public Byte convert(Number in, Context context) {
        if (in == null) return null;
        return in.byteValue();
    }

    public String toString() {
        return "NumberToByte";
    }
}
