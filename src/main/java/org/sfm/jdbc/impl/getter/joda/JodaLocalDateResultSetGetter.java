package org.sfm.jdbc.impl.getter.joda;

import org.joda.time.LocalDate;
import org.sfm.reflect.Getter;

import java.sql.ResultSet;
import java.util.Date;


public class JodaLocalDateResultSetGetter implements Getter<ResultSet, LocalDate> {
    private final Getter<ResultSet, ? extends Date> getter;

    public JodaLocalDateResultSetGetter(Getter<ResultSet, ? extends Date> getter) {
        this.getter = getter;
    }

    @Override
    public LocalDate get(ResultSet target) throws Exception {
        return new LocalDate(getter.get(target));
    }

    @Override
    public String toString() {
        return "JodaLocalDateResultSetGetter{" +
                "getter=" + getter +
                '}';
    }
}
