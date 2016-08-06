package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.reflect.Getter;

import java.util.List;

public class DatastaxListGetter<T> implements Getter<GettableByIndexData, List<T>> {

    private final int index;
    private final Class<T> type;

    public DatastaxListGetter(int index, Class<T> type) {
        this.index = index;
        this.type = type;
    }

    @Override
    public List<T> get(GettableByIndexData target) throws Exception {
        return target.getList(index, type);
    }
}
