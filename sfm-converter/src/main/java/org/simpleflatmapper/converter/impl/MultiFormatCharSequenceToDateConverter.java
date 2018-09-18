package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MultiFormatCharSequenceToDateConverter implements ContextualConverter<CharSequence, Date> {

    private final int contextIndex;
    
    public MultiFormatCharSequenceToDateConverter(int contextIndex) {
        this.contextIndex = contextIndex;
    }

    @Override
    public Date convert(CharSequence in, Context context) throws Exception {
        if (in == null || in.length() == 0) return null;
        
        SimpleDateFormat[] formats = context.context(contextIndex);
        for(int i = 0; i < formats.length; i++) {
            try {
                return formats[i].parse(in.toString());
            } catch (ParseException e) {
                // ignore
            }
        }
        throw new ParseException("Unable to parse date '" + in + "'", 0);
        
    }
}
