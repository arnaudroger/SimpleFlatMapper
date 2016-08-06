package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.reflect.Getter;

import java.util.UUID;

public class DatastaxUUIDFromStringGetter implements Getter<GettableByIndexData, UUID> {

    private final int index;

    public DatastaxUUIDFromStringGetter(int index) {
        this.index = index;
    }

    @Override
    public UUID get(GettableByIndexData target) throws Exception {
        return UUID.fromString(target.getString(index));
    }
}
