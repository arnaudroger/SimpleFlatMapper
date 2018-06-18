package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.ShortGetter;

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
        return target.getShort(index);
    }
}
