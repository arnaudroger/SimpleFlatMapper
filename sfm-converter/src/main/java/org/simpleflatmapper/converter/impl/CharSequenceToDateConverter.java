package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class CharSequenceToDateConverter implements Converter<CharSequence, Date> {

    private final int contextIndex;

    public CharSequenceToDateConverter(int contextIndex) {
        this.contextIndex = contextIndex;
    }

    @Override
    public Date convert(CharSequence in, Context context) throws Exception {
        if (in == null || in.length() == 0) return null;
        return ((SimpleDateFormat)context.context(contextIndex)).parse(in.toString());
    }
}
