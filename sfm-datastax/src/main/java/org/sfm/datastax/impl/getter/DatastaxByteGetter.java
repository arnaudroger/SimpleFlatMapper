package org.sfm.datastax.impl.getter;

import com.datastax.driver.core.GettableByIndexData;
import org.sfm.datastax.DataHelper;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.ByteGetter;
import org.sfm.reflect.primitive.ShortGetter;

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
        return DataHelper.getByte(index, target);
    }
}
