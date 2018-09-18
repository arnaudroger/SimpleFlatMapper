package org.simpleflatmapper.converter;

public interface UncheckedConverter<I, O>  extends ContextualConverter<I, O> {
	O convert(I in, Context context);
}
