package org.sfm.datastax.impl.getter;

import com.datastax.driver.core.GettableByIndexData;
import org.sfm.datastax.DataHelper;
import org.sfm.datastax.DataTypeHelper;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.LongGetter;

public class DatastaxTimeGetter implements LongGetter<GettableByIndexData>, Getter<GettableByIndexData, Long> {

    private final int index;

    public DatastaxTimeGetter(int index) {
        this.index = index;
    }

    @Override
    public Long get(GettableByIndexData target) throws Exception {
        if (target.isNull(index)) {
            return null;
        }
        return getLong(target);
    }

    @Override
    public long getLong(GettableByIndexData target) throws Exception {
        return DataHelper.getTime(index, target);
    }
}
