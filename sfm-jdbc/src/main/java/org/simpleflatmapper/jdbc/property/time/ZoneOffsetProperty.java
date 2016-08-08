package org.simpleflatmapper.jdbc.property.time;

import org.simpleflatmapper.util.Supplier;

import java.time.ZoneOffset;

public class ZoneOffsetProperty implements Supplier<ZoneOffset> {
    private final ZoneOffset offset;

    public ZoneOffsetProperty(ZoneOffset offset) {
        this.offset = offset;
    }

    @Override
    public ZoneOffset get() {
        return offset;
    }
}
