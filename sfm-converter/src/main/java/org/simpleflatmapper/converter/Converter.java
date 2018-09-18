package org.simpleflatmapper.converter;

public interface Converter<I, O> {
	O convert(I in) throws Exception;
}
