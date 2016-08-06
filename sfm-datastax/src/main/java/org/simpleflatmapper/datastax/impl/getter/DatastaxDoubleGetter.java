package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.DoubleGetter;

public class DatastaxDoubleGetter implements DoubleGetter<GettableByIndexData>, Getter<GettableByIndexData, Double> {

    private final int index;

    public DatastaxDoubleGetter(int index) {
        this.index = index;
    }

    @Override
    public Double get(GettableByIndexData target) throws Exception {
        if (target.isNull(index)) {
            return null;
        }
        return getDouble(target);
    }

    @Override
    public double getDouble(GettableByIndexData target) throws Exception {
        return target.getDouble(index);
    }
}
