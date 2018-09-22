package org.simpleflatmapper.converter;

public class ConverterToContextualAdapter<F, P> implements Converter<F, P> {
    private final ContextFactory contextFactory;
    private final ContextualConverter<? super F, ? extends P> converter;

    public ConverterToContextualAdapter(ContextualConverter<? super F, ? extends P> converter, ContextFactory contextFactory) {
        this.converter = converter;
        this.contextFactory = contextFactory;
    }

    @Override
    public P convert(F in) throws Exception {
        return converter.convert(in, contextFactory.newContext());
    }
}
