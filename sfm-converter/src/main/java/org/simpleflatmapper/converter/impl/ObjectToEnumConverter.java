package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.util.EnumHelper;

public class ObjectToEnumConverter<E extends Enum<E>> implements Converter<Object, E> {
    private final Class<E> enumClass;
    private final E[] values;

    public ObjectToEnumConverter(Class<E> enumClass) {
        this.enumClass = enumClass;
        this.values = EnumHelper.getValues(enumClass);
    }

    @Override
    public E convert(Object in) throws Exception {
        if (in == null) return null;
        if (in instanceof Number) {
            int i = ((Number)in).intValue();
            if (i < 0 || i >= values.length) {
                throw new IllegalArgumentException("Invalid ordinal value " + in + " for " + enumClass);
            }
            return values[i];
        } else {
            return Enum.valueOf(enumClass, String.valueOf(in));
        }
    }
}
