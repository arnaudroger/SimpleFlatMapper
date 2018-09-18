package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ConversionException;
import org.simpleflatmapper.converter.ContextualConverter;

import java.net.MalformedURLException;
import java.net.URL;

public class ToStringToURLConverter implements ContextualConverter<Object, URL> {

	@Override
	public URL convert(Object in, Context context) {
		try {
			return new URL(String.valueOf(in));
		} catch (MalformedURLException e) {
			throw new ConversionException(e);
		}
	}

}
