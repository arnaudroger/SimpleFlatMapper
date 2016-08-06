package org.simpleflatmapper.reflect.getter.joda;

import org.joda.time.LocalTime;
import org.simpleflatmapper.reflect.Getter;

import java.util.Date;


public class JodaLocalTimeFromObjectGetter<S> implements Getter<S, LocalTime> {
    private final Getter<S, ? extends Date> getter;

    public JodaLocalTimeFromObjectGetter(Getter<S, ? extends Date> getter) {
        this.getter = getter;
    }

    @Override
    public LocalTime get(S target) throws Exception {
        Date date = getter.get(target);
        if (date == null) return null;
        return LocalTime.fromDateFields(date);
    }

    @Override
    public String toString() {
        return "JodaLocalTimeResultSetGetter{" +
                "getter=" + getter +
                '}';
    }
}
