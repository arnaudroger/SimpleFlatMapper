package org.sfm.datastax.impl;

import com.datastax.driver.core.Row;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.DoubleGetter;

public class DoubleRowGetter implements DoubleGetter<Row>, Getter<Row, Double> {

    private final int index;

    public DoubleRowGetter(int index) {
        this.index = index;
    }

    @Override
    public Double get(Row target) throws Exception {
        if (target.isNull(index)) {
            return null;
        }
        return getDouble(target);
    }

    @Override
    public double getDouble(Row target) throws Exception {
        return target.getDouble(index);
    }
}
