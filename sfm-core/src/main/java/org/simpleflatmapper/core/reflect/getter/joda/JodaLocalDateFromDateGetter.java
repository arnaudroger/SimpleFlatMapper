package org.simpleflatmapper.core.reflect.getter.joda;

import org.joda.time.LocalDate;
import org.simpleflatmapper.core.reflect.Getter;

import java.util.Date;


public class JodaLocalDateFromDateGetter<S> implements Getter<S, LocalDate> {
    private final Getter<S, ? extends Date> getter;

    public JodaLocalDateFromDateGetter(Getter<S, ? extends Date> getter) {
        this.getter = getter;
    }

    @Override
    public LocalDate get(S target) throws Exception {
        Date date = getter.get(target);
        if (date == null) return null;

        return LocalDate.fromDateFields(date);
    }

    @Override
    public String toString() {
        return "JodaLocalDateFromDateGetter{" +
                "getter=" + getter +
                '}';
    }
}
