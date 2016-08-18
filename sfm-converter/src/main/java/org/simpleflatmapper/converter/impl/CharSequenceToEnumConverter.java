package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Converter;

public class CharSequenceToEnumConverter<E extends Enum<E>> implements Converter<CharSequence, E> {
    private final Class<E> enumClass;

    public CharSequenceToEnumConverter(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public E convert(CharSequence in) throws Exception {
        if (in == null) return null;
        return Enum.valueOf(enumClass, in.toString());
    }
}
