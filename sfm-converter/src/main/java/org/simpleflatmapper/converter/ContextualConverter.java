package org.simpleflatmapper.converter;

public interface ContextualConverter<I, O> {
	O convert(I in, Context context) throws Exception;
}
