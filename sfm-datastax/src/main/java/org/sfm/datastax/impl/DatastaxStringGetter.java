package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableByIndexData;
import org.sfm.reflect.Getter;

public class DatastaxStringGetter implements Getter<GettableByIndexData, String> {

    private final int index;

    public DatastaxStringGetter(int index) {
        this.index = index;
    }

    @Override
    public String get(GettableByIndexData target) throws Exception {
        return target.getString(index);
    }
}
