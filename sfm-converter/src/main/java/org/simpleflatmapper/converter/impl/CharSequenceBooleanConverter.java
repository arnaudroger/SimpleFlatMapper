package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;

public class CharSequenceBooleanConverter implements Converter<CharSequence, Boolean> {
    @Override
    public Boolean convert(CharSequence in, Context context) throws Exception {
        if (in == null) return null;
        return Boolean.valueOf(in.toString());
    }

    public String toString() {
        return "CharSequenceToBoolean";
    }
}
