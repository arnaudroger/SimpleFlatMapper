package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.FloatGetter;

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
