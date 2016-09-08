package org.simpleflatmapper.map.property.time;

import org.simpleflatmapper.util.Supplier;

import java.time.ZoneId;

public class JavaZoneIdProperty implements Supplier<ZoneId> {

    private final ZoneId zoneId;

    public JavaZoneIdProperty(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    @Override
    public ZoneId get() {
        return zoneId;
    }

    @Override
    public String toString() {
        return "ZoneId{" + zoneId.getId() + '}';
    }
}
