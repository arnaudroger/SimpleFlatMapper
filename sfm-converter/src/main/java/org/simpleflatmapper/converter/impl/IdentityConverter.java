package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;

public class IdentityConverter<I> implements Converter<I, I> {
    @Override
    public I convert(I in, Context context) throws Exception {
        return in;
    }
}
