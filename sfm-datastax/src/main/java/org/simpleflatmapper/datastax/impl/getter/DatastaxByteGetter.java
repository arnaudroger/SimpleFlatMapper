package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.ByteGetter;

public class DatastaxByteGetter implements ByteGetter<GettableByIndexData>, Getter<GettableByIndexData, Byte> {

    private final int index;

    public DatastaxByteGetter(int index) {
        this.index = index;
    }

    @Override
    public Byte get(GettableByIndexData target) throws Exception {
        if (target.isNull(index)) {
            return null;
        }
        return getByte(target);
    }

    @Override
    public byte getByte(GettableByIndexData target) throws Exception {
        return target.getByte(index);
    }
}
