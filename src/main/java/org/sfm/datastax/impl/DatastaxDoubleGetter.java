package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableData;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.DoubleGetter;

public class DatastaxDoubleGetter implements DoubleGetter<GettableData>, Getter<GettableData, Double> {

    private final int index;

    public DatastaxDoubleGetter(int index) {
        this.index = index;
    }

    @Override
    public Double get(GettableData target) throws Exception {
        if (target.isNull(index)) {
            return null;
        }
        return getDouble(target);
    }

    @Override
    public double getDouble(GettableData target) throws Exception {
        return target.getDouble(index);
    }
}
