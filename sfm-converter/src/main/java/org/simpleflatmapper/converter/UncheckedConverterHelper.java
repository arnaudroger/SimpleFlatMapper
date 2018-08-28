package org.simpleflatmapper.converter;

import org.simpleflatmapper.util.ErrorHelper;

public final class UncheckedConverterHelper {

    private UncheckedConverterHelper() {}

    public static <I, O> UncheckedConverter<I, O> toUnchecked(final Converter<I, O> converter) {
        return new UncheckedConverterImpl<I, O>(converter);
    }

    private static class UncheckedConverterImpl<I, O> implements UncheckedConverter<I, O> {
        private final Converter<I, O> converter;

        public UncheckedConverterImpl(Converter<I, O> converter) {
            this.converter = converter;
        }

        @Override
        public O convert(I in, Context context) {
            try {
                return converter.convert(in, context);
            } catch (Exception e) {
                return ErrorHelper.rethrow(e);
            }
        }
    }
}
