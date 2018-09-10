package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.util.Date;

public class JavaOffsetTimeTojuDateConverter implements Converter<OffsetTime, Date> {
    @Override
    public Date convert(OffsetTime in, Context context) throws Exception {
        if (in == null) return null;
        return Date.from(in.atDate(LocalDate.now()).toInstant());
    }
}
