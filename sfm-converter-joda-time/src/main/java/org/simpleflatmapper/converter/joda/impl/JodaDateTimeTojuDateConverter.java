package org.simpleflatmapper.converter.joda.impl;

import org.joda.time.DateTime;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;

import java.util.Date;

public class JodaDateTimeTojuDateConverter implements Converter<DateTime, Date> {

    @Override
    public Date convert(DateTime in, Context context) throws Exception {
        if (in == null) return null;
        return in.toDate();
    }
}
