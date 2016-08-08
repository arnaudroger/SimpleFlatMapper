package org.simpleflatmapper.converter;


public class ToStringConverter implements Converter<Object, String> {

	public static final ToStringConverter INSTANCE = new ToStringConverter();

	private ToStringConverter() {}

	@Override
	public String convert(Object in) {
		return String.valueOf(in);
	}
}
