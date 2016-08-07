package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Converter;

public class CharSequenceLongConverter implements Converter<CharSequence, Long> {
    @Override
    public Long convert(CharSequence in) throws Exception {
        if (in == null) return null;
        return Long.valueOf(in.toString());
    }

    public String toString() {
        return "CharSequenceToLong";
    }
}
