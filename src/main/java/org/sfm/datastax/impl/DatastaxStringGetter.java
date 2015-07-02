package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableData;
import org.sfm.reflect.Getter;

public class DatastaxStringGetter implements Getter<GettableData, String> {

    private final int index;

    public DatastaxStringGetter(int index) {
        this.index = index;
    }

    @Override
    public String get(GettableData target) throws Exception {
        return target.getString(index);
    }
}
