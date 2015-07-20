package org.sfm.map.getter.impl.joda;

import org.joda.time.DateTime;
import org.sfm.reflect.Getter;

import java.util.Date;


public class JodaDateTimeFromDateGetter<S> implements Getter<S, DateTime> {
    private final Getter<S, ? extends Date> getter;

    public JodaDateTimeFromDateGetter(Getter<S, ? extends Date> getter) {
        this.getter = getter;
    }

    @Override
    public DateTime get(S target) throws Exception {
        return new DateTime(getter.get(target));
    }

    @Override
    public String toString() {
        return "JodaDateTimeFromDateGetter{" +
                "getter=" + getter +
                '}';
    }
}
