package org.sfm.datastax.impl;

import com.datastax.driver.core.Row;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.LongGetter;

public class LongRowGetter implements LongGetter<Row>, Getter<Row, Long> {

    private final int index;

    public LongRowGetter(int index) {
        this.index = index;
    }

    @Override
    public Long get(Row target) throws Exception {
        if (target.isNull(index)) {
            return null;
        }
        return getLong(target);
    }

    @Override
    public long getLong(Row target) throws Exception {
        return target.getLong(index);
    }
}
