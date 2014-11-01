package org.sfm.utils.conv;


public class ToStringConverter<I> implements Converter<I, String> {
	@Override
	public String convert(I in) {
		return String.valueOf(in);
	}
}
