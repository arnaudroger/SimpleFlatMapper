package org.simpleflatmapper.jdbc.converter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;

import java.sql.Timestamp;
import java.util.Date;

public class UtilDateToTimestampConverter implements Converter<Date, Timestamp> {
    @Override
    public Timestamp convert(Date in, Context context) throws Exception {
        if (in != null) {
            return new Timestamp(in.getTime());
        }
        return null;
    }
}
