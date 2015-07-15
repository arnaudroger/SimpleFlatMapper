package org.sfm.map.impl.getter.joda;

import org.joda.time.LocalDateTime;
import org.sfm.reflect.Getter;

import java.util.Date;


public class JodaLocalDateTimeFromDateGetter<S> implements Getter<S, LocalDateTime> {
    private final Getter<S, ? extends Date> getter;

    public JodaLocalDateTimeFromDateGetter(Getter<S, ? extends Date> getter) {
        this.getter = getter;
    }

    @Override
    public LocalDateTime get(S target) throws Exception {
        return new LocalDateTime(getter.get(target));
    }

    @Override
    public String toString() {
        return "JodaLocalDateTimeFromDateGetter{" +
                "getter=" + getter +
                '}';
    }
}
