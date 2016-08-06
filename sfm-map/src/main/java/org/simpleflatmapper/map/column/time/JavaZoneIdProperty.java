package org.simpleflatmapper.map.column.time;

import org.simpleflatmapper.map.column.ColumnProperty;
import org.simpleflatmapper.util.date.time.ZoneIdSupplier;

import java.time.ZoneId;

public class JavaZoneIdProperty implements ColumnProperty, ZoneIdSupplier {

    private final ZoneId zoneId;

    public JavaZoneIdProperty(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public ZoneId get() {
        return zoneId;
    }
}
