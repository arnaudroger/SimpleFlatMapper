package org.simpleflatmapper.converter.joda;

import org.joda.time.DateTimeZone;
import org.simpleflatmapper.util.Supplier;


public class JodaDateTimeZoneProperty implements Supplier<DateTimeZone> {
    private final DateTimeZone zone;

    public JodaDateTimeZoneProperty(DateTimeZone zone) {
        this.zone = zone;
    }

    public DateTimeZone get() {
        return zone;
    }
}
