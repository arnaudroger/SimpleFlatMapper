package org.simpleflatmapper.reflect.getter.joda;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.simpleflatmapper.reflect.Getter;

import java.util.Date;


public class JodaDateTimeFromDateGetter<S> implements Getter<S, DateTime> {
    private final Getter<S, ? extends Date> getter;
    private final DateTimeZone dateTimeZone;

    public JodaDateTimeFromDateGetter(Getter<S, ? extends Date> getter, DateTimeZone dateTimeZone) {
        this.getter = getter;
        this.dateTimeZone = dateTimeZone;
    }

    @Override
    public DateTime get(S target) throws Exception {
        Date date = getter.get(target);
        if (date == null) return null;
        return new DateTime(date, dateTimeZone);
    }

    @Override
    public String toString() {
        return "JodaDateTimeFromDateGetter{" +
                "getter=" + getter +
                '}';
    }
}
