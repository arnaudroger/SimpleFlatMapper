package org.simpleflatmapper.jdbc.converter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.util.Date;

public class UtilDateToDateConverter implements ContextualConverter<Date, java.sql.Date> {
    @Override
    public java.sql.Date convert(Date in, Context context) throws Exception {
        if (in != null) {
            return new java.sql.Date(in.getTime());
        }
        return null;
    }
}
