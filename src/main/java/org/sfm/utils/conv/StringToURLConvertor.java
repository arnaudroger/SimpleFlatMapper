package org.sfm.utils.conv;

import java.net.MalformedURLException;
import java.net.URL;

public class StringToURLConvertor<I> implements Converter<I, URL> {

	@Override
	public URL convert(I in) {
		try {
			return new URL(String.valueOf(in));
		} catch (MalformedURLException e) {
			throw new ConversionException(e);
		}
	}

}
