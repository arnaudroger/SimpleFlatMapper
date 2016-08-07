package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.ConversionException;
import org.simpleflatmapper.converter.Converter;

import java.net.MalformedURLException;
import java.net.URL;

public class ToStringToURLConverter<I> implements Converter<I, URL> {

	@Override
	public URL convert(I in) {
		try {
			return new URL(String.valueOf(in));
		} catch (MalformedURLException e) {
			throw new ConversionException(e);
		}
	}

}
