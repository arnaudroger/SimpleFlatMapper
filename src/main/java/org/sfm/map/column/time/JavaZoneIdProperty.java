package org.sfm.map.column.time;

import org.sfm.map.column.ColumnProperty;

import java.time.ZoneId;

public class JavaZoneIdProperty implements ColumnProperty {

    private final ZoneId zoneId;

    public JavaZoneIdProperty(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }
}
