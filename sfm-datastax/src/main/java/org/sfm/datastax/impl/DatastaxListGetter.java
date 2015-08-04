package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableData;
import org.sfm.reflect.Getter;

import java.util.List;

public class DatastaxListGetter<T> implements Getter<GettableData, List<T>> {

    private final int index;
    private final Class<T> type;

    public DatastaxListGetter(int index, Class<T> type) {
        this.index = index;
        this.type = type;
    }

    @Override
    public List<T> get(GettableData target) throws Exception {
        return target.getList(index, type);
    }
}
