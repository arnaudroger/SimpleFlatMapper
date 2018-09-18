package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

public class CharSequenceShortConverter implements ContextualConverter<CharSequence, Short> {
    @Override
    public Short convert(CharSequence in, Context context) throws Exception {
        if (in == null) return null;
        return Short.valueOf(in.toString());
    }

    public String toString() {
        return "CharSequenceToShort";
    }
}
