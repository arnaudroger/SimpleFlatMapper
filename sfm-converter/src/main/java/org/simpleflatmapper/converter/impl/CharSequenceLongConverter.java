package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

public class CharSequenceLongConverter implements ContextualConverter<CharSequence, Long> {
    @Override
    public Long convert(CharSequence in, Context context) throws Exception {
        if (in == null) return null;
        return Long.valueOf(in.toString());
    }

    public String toString() {
        return "CharSequenceToLong";
    }
}
