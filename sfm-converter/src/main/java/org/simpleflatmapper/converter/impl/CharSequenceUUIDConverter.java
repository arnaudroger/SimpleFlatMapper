package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.util.UUID;

public class CharSequenceUUIDConverter implements ContextualConverter<CharSequence, UUID> {
    @Override
    public UUID convert(CharSequence in, Context context) throws Exception {
        if (in == null) return null;
        return UUID.fromString(in.toString());
    }

    public String toString() {
        return "CharSequenceToUUID";
    }
}
