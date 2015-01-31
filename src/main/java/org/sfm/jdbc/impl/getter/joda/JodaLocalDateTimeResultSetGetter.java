package org.sfm.jdbc.impl.getter.joda;

import org.joda.time.LocalDateTime;
import org.sfm.reflect.Getter;

import java.sql.ResultSet;
import java.util.Date;


public class JodaLocalDateTimeResultSetGetter implements Getter<ResultSet, LocalDateTime> {
    private final Getter<ResultSet, ? extends Date> getter;

    public JodaLocalDateTimeResultSetGetter(Getter<ResultSet, ? extends Date> getter) {
        this.getter = getter;
    }

    @Override
    public LocalDateTime get(ResultSet target) throws Exception {
        return new LocalDateTime(getter.get(target));
    }
}
