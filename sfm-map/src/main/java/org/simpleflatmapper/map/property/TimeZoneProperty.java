package org.simpleflatmapper.map.property;


import org.simpleflatmapper.util.Supplier;

import java.util.TimeZone;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public class TimeZoneProperty implements Supplier<TimeZone> {
    private final TimeZone timeZone;


    public TimeZoneProperty(TimeZone timeZone) {
        this.timeZone = requireNonNull("timeZone", timeZone);
    }

    public TimeZone get() {
        return timeZone;
    }

    @Override
    public String toString() {
        return "TimeZone{"+ timeZone.getID() + '}';
    }
}
