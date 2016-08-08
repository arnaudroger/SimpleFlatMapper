package org.simpleflatmapper.converter;

public class ComposedConverter<I, J, O> implements Converter<I, O> {

    private final Converter<? super I, ? extends J> c1;
    private final Converter<? super J, ? extends O> c2;

    public ComposedConverter(Converter<? super I, ? extends J> c1, Converter<? super J, ? extends O> c2) {
        this.c1 = c1;
        this.c2 = c2;
    }


    @Override
    public O convert(I in) throws Exception {
        return c2.convert(c1.convert(in));
    }
}
