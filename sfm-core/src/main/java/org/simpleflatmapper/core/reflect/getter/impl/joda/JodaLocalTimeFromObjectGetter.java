package org.simpleflatmapper.core.reflect.getter.impl.joda;

import org.joda.time.LocalTime;
import org.simpleflatmapper.core.reflect.Getter;

import java.util.Date;


public class JodaLocalTimeFromObjectGetter<S> implements Getter<S, LocalTime> {
    private final Getter<S, ? extends Date> getter;

    public JodaLocalTimeFromObjectGetter(Getter<S, ? extends Date> getter) {
        this.getter = getter;
    }

    @Override
    public LocalTime get(S target) throws Exception {
        return new LocalTime(getter.get(target));
    }

    @Override
    public String toString() {
        return "JodaLocalTimeResultSetGetter{" +
                "getter=" + getter +
                '}';
    }
}
