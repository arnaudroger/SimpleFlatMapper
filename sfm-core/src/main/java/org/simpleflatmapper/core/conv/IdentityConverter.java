package org.simpleflatmapper.core.conv;

public class IdentityConverter<I> implements Converter<I, I> {
    @Override
    public I convert(I in) throws Exception {
        return in;
    }
}
