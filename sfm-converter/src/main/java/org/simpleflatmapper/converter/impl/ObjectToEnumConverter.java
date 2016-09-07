package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.util.EnumHelper;

public class ObjectToEnumConverter<E extends Enum<E>> implements Converter<Object, E> {
    private final Class<E> enumClass;

    private final NumberToEnumConverter<E> numberToEnumConverter;

    public ObjectToEnumConverter(Class<E> enumClass) {
        this.enumClass = enumClass;
        this.numberToEnumConverter = new NumberToEnumConverter<E>(enumClass);
    }

    @Override
    public E convert(Object in) throws Exception {
        if (in == null) return null;
        if (in instanceof Number) {
            return numberToEnumConverter.convert((Number) in);
        } else {
            return Enum.valueOf(enumClass, String.valueOf(in));
        }
    }
}
