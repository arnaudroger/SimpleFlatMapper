package org.sfm.datastax.impl.getter;

import com.datastax.driver.core.GettableByIndexData;
import org.sfm.datastax.DataHelper;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.IntGetter;
import org.sfm.reflect.primitive.ShortGetter;

public class DatastaxShortGetter implements ShortGetter<GettableByIndexData>, Getter<GettableByIndexData, Short> {

    private final int index;

    public DatastaxShortGetter(int index) {
        this.index = index;
    }

    @Override
    public Short get(GettableByIndexData target) throws Exception {
        if (target.isNull(index)) {
            return null;
        }
        return getShort(target);
    }

    @Override
    public short getShort(GettableByIndexData target) throws Exception {
        return DataHelper.getShort(index, target);
    }
}
