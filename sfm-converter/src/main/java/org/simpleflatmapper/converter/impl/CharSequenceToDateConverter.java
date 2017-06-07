package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Converter;

import java.text.SimpleDateFormat;
import java.util.Date;


public class CharSequenceToDateConverter implements Converter<CharSequence, Date> {

    private final String format;

    public CharSequenceToDateConverter(String format) {
        this.format = format;
    }

    @Override
    public Date convert(CharSequence in) throws Exception {
        if (in == null || in.length() == 0) return null;
        return new SimpleDateFormat(format).parse(in.toString());
    }
}
