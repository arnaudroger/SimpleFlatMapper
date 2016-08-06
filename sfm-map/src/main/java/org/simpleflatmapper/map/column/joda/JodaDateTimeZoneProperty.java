package org.simpleflatmapper.map.column.joda;

import org.joda.time.DateTimeZone;
import org.simpleflatmapper.map.column.ColumnProperty;
import org.simpleflatmapper.util.date.joda.DateTimeZoneSupplier;


public class JodaDateTimeZoneProperty implements ColumnProperty, DateTimeZoneSupplier {
    private final DateTimeZone zone;

    public JodaDateTimeZoneProperty(DateTimeZone zone) {
        this.zone = zone;
    }

    public DateTimeZone get() {
        return zone;
    }
}
