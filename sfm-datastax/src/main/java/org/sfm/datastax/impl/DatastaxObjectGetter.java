package org.sfm.datastax.impl;


import com.datastax.driver.core.GettableByIndexData;
import org.sfm.reflect.Getter;

public class DatastaxObjectGetter implements Getter<GettableByIndexData, Object> {

    private final int index;

    public DatastaxObjectGetter(int index) {
        this.index = index;
    }

    @Override
    public Object get(GettableByIndexData target) throws Exception {
        return target.getObject(index);
    }
}
