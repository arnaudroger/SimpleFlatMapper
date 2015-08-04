package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableByIndexData;
import com.datastax.driver.core.GettableByIndexData;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.IntGetter;

public class DatastaxIntegerGetter implements IntGetter<GettableByIndexData>, Getter<GettableByIndexData, Integer> {

    private final int index;

    public DatastaxIntegerGetter(int index) {
        this.index = index;
    }

    @Override
    public Integer get(GettableByIndexData target) throws Exception {
        if (target.isNull(index)) {
            return null;
        }
        return getInt(target);
    }

    @Override
    public int getInt(GettableByIndexData target) throws Exception {
        return target.getInt(index);
    }
}
