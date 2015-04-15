package org.sfm.map.column;


import java.util.TimeZone;

public class TimeZoneProperty implements ColumnProperty {
    private final TimeZone timeZone;


    public TimeZoneProperty(TimeZone timeZone) {
        if (timeZone == null) throw new NullPointerException();
        this.timeZone = timeZone;
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
