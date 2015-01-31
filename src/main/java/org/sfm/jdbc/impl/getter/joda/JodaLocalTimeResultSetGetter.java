package org.sfm.jdbc.impl.getter.joda;

import org.joda.time.LocalTime;
import org.sfm.reflect.Getter;

import java.sql.ResultSet;
import java.util.Date;


public class JodaLocalTimeResultSetGetter implements Getter<ResultSet, LocalTime> {
    private final Getter<ResultSet, ? extends Date> getter;

    public JodaLocalTimeResultSetGetter(Getter<ResultSet, ? extends Date> getter) {
        this.getter = getter;
    }

    @Override
    public LocalTime get(ResultSet target) throws Exception {
        return new LocalTime(getter.get(target));
    }
}
