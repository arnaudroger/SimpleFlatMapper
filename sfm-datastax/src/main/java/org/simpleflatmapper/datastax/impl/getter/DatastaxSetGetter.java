package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.reflect.Getter;

import java.util.Set;

public class DatastaxSetGetter<T> implements Getter<GettableByIndexData, Set<T>> {

    private final int index;
    private final Class<T> type;

    public DatastaxSetGetter(int index, Class<T> type) {
        this.index = index;
        this.type = type;
    }

    @Override
    public Set<T> get(GettableByIndexData target) throws Exception {

        return target.getSet(index, type);
    }
}
