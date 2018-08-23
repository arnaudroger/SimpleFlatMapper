package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class MultiFormatCharSequenceToDateConverter implements Converter<CharSequence, Date> {

    private final SimpleDateFormat[] formats;

    public MultiFormatCharSequenceToDateConverter(List<String> formats, TimeZone timeZone) {
        this.formats = new SimpleDateFormat[formats.size()];
        for(int i = 0; i < formats.size(); i++) {
            SimpleDateFormat f = new SimpleDateFormat(formats.get(formats.size() - i - 1));
            f.setTimeZone(timeZone);
            this.formats[i] = f;
        }
    }

    @Override
    public Date convert(CharSequence in) throws Exception {
        if (in == null || in.length() == 0) return null;
        
        for(int i = 0; i < formats.length; i++) {
            try {
                return ((SimpleDateFormat)formats[i].clone()).parse(in.toString());
            } catch (ParseException e) {
                // ignore
            }
        }
        throw new ParseException("Unable to parse date '" + in + "'", 0);
        
    }
}
