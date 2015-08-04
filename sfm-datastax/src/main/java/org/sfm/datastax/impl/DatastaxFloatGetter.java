package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableByIndexData;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.FloatGetter;

public class DatastaxFloatGetter implements FloatGetter<GettableByIndexData>, Getter<GettableByIndexData, Float> {

    private final int index;

    public DatastaxFloatGetter(int index) {
        this.index = index;
    }

    @Override
    public Float get(GettableByIndexData target) throws Exception {
        if (target.isNull(index)) {
            return null;
        }
        return getFloat(target);
    }

    @Override
    public float getFloat(GettableByIndexData target) throws Exception {
        return target.getFloat(index);
    }
}
