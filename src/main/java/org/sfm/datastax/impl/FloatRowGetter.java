package org.sfm.datastax.impl;

import com.datastax.driver.core.Row;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.FloatGetter;

public class FloatRowGetter implements FloatGetter<Row>, Getter<Row, Float> {

    private final int index;

    public FloatRowGetter(int index) {
        this.index = index;
    }

    @Override
    public Float get(Row target) throws Exception {
        if (target.isNull(index)) {
            return null;
        }
        return getFloat(target);
    }

    @Override
    public float getFloat(Row target) throws Exception {
        return target.getFloat(index);
    }
}
