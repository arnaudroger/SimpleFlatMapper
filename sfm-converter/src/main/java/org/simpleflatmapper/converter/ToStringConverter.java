package org.simpleflatmapper.converter;


public class ToStringConverter<I> implements Converter<I, String> {
	@Override
	public String convert(I in) {
		return String.valueOf(in);
	}
}
