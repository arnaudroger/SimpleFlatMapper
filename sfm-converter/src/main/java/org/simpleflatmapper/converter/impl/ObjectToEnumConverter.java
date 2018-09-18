package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

public class ObjectToEnumConverter<E extends Enum<E>> implements ContextualConverter<Object, E> {
    private final Class<E> enumClass;

    private final NumberToEnumConverter<E> numberToEnumConverter;

    public ObjectToEnumConverter(Class<E> enumClass) {
        this.enumClass = enumClass;
        this.numberToEnumConverter = new NumberToEnumConverter<E>(enumClass);
    }

    @Override
    public E convert(Object in, Context context) throws Exception {
        if (in == null) return null;
        if (in instanceof Number) {
            return numberToEnumConverter.convert((Number) in, context);
        } else {
            return Enum.valueOf(enumClass, String.valueOf(in));
        }
    }
}
