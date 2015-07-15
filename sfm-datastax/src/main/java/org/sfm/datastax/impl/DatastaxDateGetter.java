package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableData;
import org.sfm.reflect.Getter;

import java.util.Date;

public class DatastaxDateGetter implements Getter<GettableData, Date> {

    private final int index;

    public DatastaxDateGetter(int index) {
        this.index = index;
    }

    @Override
    public Date get(GettableData target) throws Exception {
        return target.getDate(index);
    }
}
