package org.simpleflatmapper.converter.impl;


import org.simpleflatmapper.converter.Converter;

public class ToStringConverter<I> implements Converter<I, String> {
	@Override
	public String convert(I in) {
		return String.valueOf(in);
	}
}
