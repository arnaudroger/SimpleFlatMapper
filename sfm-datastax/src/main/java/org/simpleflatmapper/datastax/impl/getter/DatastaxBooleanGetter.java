package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.BooleanGetter;

public class DatastaxBooleanGetter implements BooleanGetter<GettableByIndexData>, Getter<GettableByIndexData, Boolean> {

    private final int index;

    public DatastaxBooleanGetter(int index) {
        this.index = index;
    }

    @Override
    public Boolean get(GettableByIndexData target) throws Exception {
        if (target.isNull(index)) {
            return null;
        }
        return getBoolean(target);
    }

    @Override
    public boolean getBoolean(GettableByIndexData target) throws Exception {
        return target.getBool(index);
    }
}
