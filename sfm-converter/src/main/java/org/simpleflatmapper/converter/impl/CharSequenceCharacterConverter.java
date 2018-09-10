package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;

public class CharSequenceCharacterConverter implements Converter<CharSequence, Character> {
    @Override
    public Character convert(CharSequence in, Context context) throws Exception {
        if (in == null) return null;
        return Character.valueOf((char) Integer.parseInt(in.toString()));
    }

    public String toString() {
        return "CharSequenceToCharacter";
    }
}
