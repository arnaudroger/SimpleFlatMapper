package org.simpleflatmapper.map.column.time;

import org.simpleflatmapper.converter.impl.time.ZoneIdSupplier;
import org.simpleflatmapper.map.column.ColumnProperty;

import java.time.ZoneId;

public class JavaZoneIdProperty implements ColumnProperty, ZoneIdSupplier {

    private final ZoneId zoneId;

    public JavaZoneIdProperty(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    @Override
    public ZoneId get() {
        return zoneId;
    }
}
