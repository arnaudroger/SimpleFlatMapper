package org.simpleflatmapper.reflect;

import org.simpleflatmapper.converter.Converter;

public class GetterWithConverter<T, I, P> implements Getter<T, P> {

    private final Converter<? super I, ? extends P> converter;
    private final Getter<? super T, ? extends I> getter;

    public GetterWithConverter(Converter<? super I, ? extends P > converter, Getter<? super T, ? extends I> getter) {
        this.converter = converter;
        this.getter = getter;
    }

    @Override
    public P get(T target) throws Exception {
        I in = getter.get(target);
        return converter.convert(in);
    }
}
