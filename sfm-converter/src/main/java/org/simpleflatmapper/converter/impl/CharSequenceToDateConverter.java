package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Converter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class CharSequenceToDateConverter implements Converter<CharSequence, Date> {

    private final SimpleDateFormat format;

    public CharSequenceToDateConverter(String format, TimeZone timeZone) {
        this.format = new SimpleDateFormat(format);
        this.format.setTimeZone(timeZone);
    }

    @Override
    public Date convert(CharSequence in) throws Exception {
        if (in == null || in.length() == 0) return null;
        return ((SimpleDateFormat)format.clone()).parse(in.toString());
    }
}
