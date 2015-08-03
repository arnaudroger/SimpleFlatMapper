package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableData;
import org.sfm.reflect.Getter;

import java.util.Set;

public class DatastaxSetGetter<T> implements Getter<GettableData, Set<T>> {

    private final int index;
    private final Class<T> type;

    public DatastaxSetGetter(int index, Class<T> type) {
        this.index = index;
        this.type = type;
    }

    @Override
    public Set<T> get(GettableData target) throws Exception {

        return target.getSet(index, type);
    }
}
