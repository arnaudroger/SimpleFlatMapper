package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableByIndexData;
import org.sfm.reflect.Getter;

import java.util.Date;

public class DatastaxDateGetter implements Getter<GettableByIndexData, Date> {

    private final int index;

    public DatastaxDateGetter(int index) {
        this.index = index;
    }

    @Override
    public Date get(GettableByIndexData target) throws Exception {
        return target.getDate(index);
    }
}
