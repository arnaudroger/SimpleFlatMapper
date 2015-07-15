package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableData;
import org.sfm.reflect.Getter;

import java.util.UUID;

public class DatastaxUUIDGetter implements Getter<GettableData, UUID> {

    private final int index;

    public DatastaxUUIDGetter(int index) {
        this.index = index;
    }

    @Override
    public UUID get(GettableData target) throws Exception {
        return target.getUUID(index);
    }
}
