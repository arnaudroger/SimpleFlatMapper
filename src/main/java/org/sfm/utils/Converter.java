package org.sfm.utils;

public interface Converter<I, O> {
	O convert(I in);
}
