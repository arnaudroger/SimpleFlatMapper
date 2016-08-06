package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.reflect.Getter;

import java.util.UUID;

public class DatastaxUUIDGetter implements Getter<GettableByIndexData, UUID> {

    private final int index;

    public DatastaxUUIDGetter(int index) {
        this.index = index;
    }

    @Override
    public UUID get(GettableByIndexData target) throws Exception {
        return target.getUUID(index);
    }
}
