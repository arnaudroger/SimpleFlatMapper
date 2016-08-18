package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.reflect.Getter;

import static java.util.Objects.requireNonNull;

public class GetterWithConverter<T, I, P> implements Getter<T, P> {

    private final Converter<? super I, ? extends P> converter;
    private final Getter<? super T, ? extends I> getter;

    public GetterWithConverter(Converter<? super I, ? extends P > converter, Getter<? super T, ? extends I> getter) {
        this.converter = requireNonNull(converter, "converter");
        this.getter = requireNonNull(getter, "getter");
    }

    @Override
    public P get(T target) throws Exception {
        I in = getter.get(target);
        return converter.convert(in);
    }
}
