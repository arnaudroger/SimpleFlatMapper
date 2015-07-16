package org.sfm.datastax.impl;


import com.datastax.driver.core.GettableData;
import org.sfm.reflect.Getter;

public class DatastaxObjectGetter implements Getter<GettableData, Object> {

    private final int index;

    public DatastaxObjectGetter(int index) {
        this.index = index;
    }

    @Override
    public Object get(GettableData target) throws Exception {
        return target.getObject(index);
    }
}
