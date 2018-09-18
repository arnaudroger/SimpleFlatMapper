package org.simpleflatmapper.converter;

public class ContextualConverterAdapter<I, O> implements ContextualConverter<I, O> {
    private final Converter<? super I, ? extends O> delegate;

    public ContextualConverterAdapter(Converter<? super I, ? extends O> converter) {
        this.delegate = converter;
    }

    @Override
    public O convert(I in, Context context) throws Exception {
        return delegate.convert(in);
    }
}
