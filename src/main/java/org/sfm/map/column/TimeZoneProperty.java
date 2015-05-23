package org.sfm.map.column;


import java.util.TimeZone;

import static org.sfm.utils.Asserts.requireNonNull;

public class TimeZoneProperty implements ColumnProperty {
    private final TimeZone timeZone;


    public TimeZoneProperty(TimeZone timeZone) {
        this.timeZone = requireNonNull("timeZone", timeZone);
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    @Override
    public String toString() {
        return "TimeZone{"+ timeZone.getDisplayName() +
                '}';
    }
}
