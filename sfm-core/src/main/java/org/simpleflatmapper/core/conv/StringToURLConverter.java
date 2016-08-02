package org.simpleflatmapper.core.conv;

import java.net.MalformedURLException;
import java.net.URL;

public class StringToURLConverter<I> implements Converter<I, URL> {

	@Override
	public URL convert(I in) {
		try {
			return new URL(String.valueOf(in));
		} catch (MalformedURLException e) {
			throw new ConversionException(e);
		}
	}

}
