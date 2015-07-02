package org.sfm.datastax.impl;

import com.datastax.driver.core.Row;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.IntGetter;

public class IntegerRowGetter implements IntGetter<Row>, Getter<Row, Integer> {

    private final int index;

    public IntegerRowGetter(int index) {
        this.index = index;
    }

    @Override
    public Integer get(Row target) throws Exception {
        if (target.isNull(index)) {
            return null;
        }
        return getInt(target);
    }

    @Override
    public int getInt(Row target) throws Exception {
        return target.getInt(index);
    }
}
