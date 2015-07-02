package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableData;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.IntGetter;

public class DatastaxIntegerGetter implements IntGetter<GettableData>, Getter<GettableData, Integer> {

    private final int index;

    public DatastaxIntegerGetter(int index) {
        this.index = index;
    }

    @Override
    public Integer get(GettableData target) throws Exception {
        if (target.isNull(index)) {
            return null;
        }
        return getInt(target);
    }

    @Override
    public int getInt(GettableData target) throws Exception {
        return target.getInt(index);
    }
}
