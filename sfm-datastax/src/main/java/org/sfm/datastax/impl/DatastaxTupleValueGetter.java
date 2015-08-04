package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableByIndexData;
import com.datastax.driver.core.TupleValue;
import org.sfm.reflect.Getter;

public class DatastaxTupleValueGetter implements Getter<GettableByIndexData, TupleValue> {

    private final int index;

    public DatastaxTupleValueGetter(int index) {
        this.index = index;
    }

    @Override
    public TupleValue get(GettableByIndexData target) throws Exception {
        return target.getTupleValue(index);
    }
}
