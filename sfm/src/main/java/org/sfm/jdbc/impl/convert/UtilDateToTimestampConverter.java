package org.sfm.jdbc.impl.convert;

import java.sql.Timestamp;
import java.util.Date;

public class UtilDateToTimestampConverter implements org.sfm.utils.conv.Converter<Date, Timestamp> {
    @Override
    public Timestamp convert(Date in) throws Exception {
        if (in != null) {
            return new Timestamp(in.getTime());
        }
        return null;
    }
}
