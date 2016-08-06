package org.simpleflatmapper.reflect.getter.joda;

import org.joda.time.LocalDateTime;
import org.simpleflatmapper.reflect.Getter;

import java.util.Date;


public class JodaLocalDateTimeFromDateGetter<S> implements Getter<S, LocalDateTime> {
    private final Getter<S, ? extends Date> getter;

    public JodaLocalDateTimeFromDateGetter(Getter<S, ? extends Date> getter) {
        this.getter = getter;
    }

    @Override
    public LocalDateTime get(S target) throws Exception {
        Date date = getter.get(target);
        if (date == null) return null;

        return LocalDateTime.fromDateFields(date);
    }

    @Override
    public String toString() {
        return "JodaLocalDateTimeFromDateGetter{" +
                "getter=" + getter +
                '}';
    }
}
