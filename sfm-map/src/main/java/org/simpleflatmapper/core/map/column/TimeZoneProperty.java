package org.simpleflatmapper.core.map.column;


import org.simpleflatmapper.util.date.TimeZoneSupplier;

import java.util.TimeZone;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public class TimeZoneProperty implements ColumnProperty, TimeZoneSupplier {
    private final TimeZone timeZone;


    public TimeZoneProperty(TimeZone timeZone) {
        this.timeZone = requireNonNull("timeZone", timeZone);
    }

    public TimeZone get() {
        return timeZone;
    }

    @Override
    public String toString() {
        return "TimeZone{"+ timeZone.getDisplayName() +
                '}';
    }
}
