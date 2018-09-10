package org.simpleflatmapper.converter;

public interface UncheckedConverter<I, O>  extends Converter<I, O> {
	O convert(I in, Context context);
}
