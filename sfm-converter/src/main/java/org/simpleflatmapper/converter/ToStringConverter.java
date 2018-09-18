package org.simpleflatmapper.converter;


public class ToStringConverter implements ContextualConverter<Object, String> {

	public static final ToStringConverter INSTANCE = new ToStringConverter();

	private ToStringConverter() {}

	@Override
	public String convert(Object in, Context context) {
		return String.valueOf(in);
	}
}
