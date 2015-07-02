package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableData;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.FloatGetter;

public class DatastaxFloatGetter implements FloatGetter<GettableData>, Getter<GettableData, Float> {

    private final int index;

    public DatastaxFloatGetter(int index) {
        this.index = index;
    }

    @Override
    public Float get(GettableData target) throws Exception {
        if (target.isNull(index)) {
            return null;
        }
        return getFloat(target);
    }

    @Override
    public float getFloat(GettableData target) throws Exception {
        return target.getFloat(index);
    }
}
