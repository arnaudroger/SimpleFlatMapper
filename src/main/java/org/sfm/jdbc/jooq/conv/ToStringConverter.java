package org.sfm.jdbc.jooq.conv;

import org.sfm.utils.Converter;

public class ToStringConverter<I> implements Converter<I, String> {
	@Override
	public String convert(I in) {
		return String.valueOf(in);
	}
}
