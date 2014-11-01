package org.sfm.utils.conv;

public interface Converter<I, O> {
	O convert(I in);
}
