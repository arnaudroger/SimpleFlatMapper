package org.sfm.map.impl.getter.joda;

import org.joda.time.LocalDate;
import org.sfm.reflect.Getter;

import java.util.Date;


public class JodaLocalDateFromDateGetter<S> implements Getter<S, LocalDate> {
    private final Getter<S, ? extends Date> getter;

    public JodaLocalDateFromDateGetter(Getter<S, ? extends Date> getter) {
        this.getter = getter;
    }

    @Override
    public LocalDate get(S target) throws Exception {
        return new LocalDate(getter.get(target));
    }

    @Override
    public String toString() {
        return "JodaLocalDateFromDateGetter{" +
                "getter=" + getter +
                '}';
    }
}
