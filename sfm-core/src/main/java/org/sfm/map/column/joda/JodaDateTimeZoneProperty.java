package org.sfm.map.column.joda;

import org.joda.time.DateTimeZone;
import org.sfm.map.column.ColumnProperty;


public class JodaDateTimeZoneProperty implements ColumnProperty {
    private final DateTimeZone zone;

    public JodaDateTimeZoneProperty(DateTimeZone zone) {
        this.zone = zone;
    }

    public DateTimeZone getZone() {
        return zone;
    }
}
