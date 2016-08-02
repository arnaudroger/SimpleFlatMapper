package org.simpleflatmapper.jdbc.impl.convert;

import org.simpleflatmapper.core.conv.Converter;

import java.sql.Timestamp;
import java.util.Date;

public class UtilDateToTimestampConverter implements Converter<Date, Timestamp> {
    @Override
    public Timestamp convert(Date in) throws Exception {
        if (in != null) {
            return new Timestamp(in.getTime());
        }
        return null;
    }
}
