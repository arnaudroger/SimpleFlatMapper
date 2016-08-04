package org.simpleflatmapper.converter;

public class IdentityConverter<I> implements Converter<I, I> {
    @Override
    public I convert(I in) throws Exception {
        return in;
    }
}
