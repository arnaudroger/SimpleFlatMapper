package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableData;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.LongGetter;

public class DatastaxLongGetter implements LongGetter<GettableData>, Getter<GettableData, Long> {

    private final int index;

    public DatastaxLongGetter(int index) {
        this.index = index;
    }

    @Override
    public Long get(GettableData target) throws Exception {
        if (target.isNull(index)) {
            return null;
        }
        return getLong(target);
    }

    @Override
    public long getLong(GettableData target) throws Exception {
        return target.getLong(index);
    }
}
