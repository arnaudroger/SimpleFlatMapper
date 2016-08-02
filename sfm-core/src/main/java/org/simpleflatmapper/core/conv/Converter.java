package org.simpleflatmapper.core.conv;

public interface Converter<I, O> {
	O convert(I in) throws Exception;
}
