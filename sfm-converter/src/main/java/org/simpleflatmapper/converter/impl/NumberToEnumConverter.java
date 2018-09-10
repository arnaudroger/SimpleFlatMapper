package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.util.EnumHelper;

public class NumberToEnumConverter<E extends Enum<E>> implements Converter<Number, E> {
    private final Class<E> enumClass;
    private final E[] values;

    public NumberToEnumConverter(Class<E> enumClass) {
        this.enumClass = enumClass;
        this.values = EnumHelper.getValues(enumClass);
    }

    @Override
    public E convert(Number in, Context context) throws Exception {
        if (in == null) return null;
        int i = in.intValue();
        if (i < 0 || i >= values.length) {
            throw new IllegalArgumentException("Invalid ordinal value " + in  + " for " + enumClass);
        }
        return values[i];
    }
}
