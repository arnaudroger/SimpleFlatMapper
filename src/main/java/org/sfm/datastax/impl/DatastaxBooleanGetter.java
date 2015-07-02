package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableData;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.BooleanGetter;

public class DatastaxBooleanGetter implements BooleanGetter<GettableData>, Getter<GettableData, Boolean> {

    private final int index;

    public DatastaxBooleanGetter(int index) {
        this.index = index;
    }

    @Override
    public Boolean get(GettableData target) throws Exception {
        if (target.isNull(index)) {
            return null;
        }
        return getBoolean(target);
    }

    @Override
    public boolean getBoolean(GettableData target) throws Exception {
        return target.getBool(index);
    }
}
