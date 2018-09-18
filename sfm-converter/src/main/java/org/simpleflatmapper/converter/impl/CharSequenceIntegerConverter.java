package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

public class CharSequenceIntegerConverter implements ContextualConverter<CharSequence, Integer> {
    @Override
    public Integer convert(CharSequence in, Context context) throws Exception {
        if (in == null) return null;
        return Integer.valueOf(in.toString());
    }

    public String toString() {
        return "CharSequenceToInteger";
    }
}
