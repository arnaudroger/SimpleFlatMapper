package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

public class IdentityConverter<I> implements ContextualConverter<I, I> {
    @Override
    public I convert(I in, Context context) throws Exception {
        return in;
    }
}
