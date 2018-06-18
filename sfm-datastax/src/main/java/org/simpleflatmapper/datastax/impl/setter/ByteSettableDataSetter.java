package org.simpleflatmapper.datastax.impl.setter;

import com.datastax.driver.core.SettableByIndexData;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.ByteSetter;

public class ByteSettableDataSetter implements Setter<SettableByIndexData, Byte>, ByteSetter<SettableByIndexData> {
    private final int index;

    public ByteSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void setByte(SettableByIndexData target, byte value) throws Exception {
        target.setByte(index, value);
    }

    @Override
    public void set(SettableByIndexData target, Byte value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            setByte(target, value);
        }
    }
}
