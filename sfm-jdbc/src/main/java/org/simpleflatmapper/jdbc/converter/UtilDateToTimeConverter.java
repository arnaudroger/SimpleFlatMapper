package org.simpleflatmapper.jdbc.converter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.sql.Time;
import java.util.Date;

public class UtilDateToTimeConverter implements ContextualConverter<Date, Time> {
    @Override
    public Time convert(Date in, Context context) throws Exception {
        if (in != null) {
            return new Time(in.getTime());
        }
        return null;
    }
}
