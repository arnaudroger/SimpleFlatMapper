package org.simpleflatmapper.jdbc.converter;

import org.simpleflatmapper.converter.Converter;

import java.sql.Timestamp;
import java.util.Date;

public class UtilDateToDateConverter implements Converter<Date, java.sql.Date> {
    @Override
    public java.sql.Date convert(Date in) throws Exception {
        if (in != null) {
            return new java.sql.Date(in.getTime());
        }
        return null;
    }
}
