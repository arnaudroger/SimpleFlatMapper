package org.sfm.datastax.impl.setter;

import com.datastax.driver.core.SettableData;
import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.LongSetter;

public class LongSettableDataSetter implements Setter<SettableData, Long>, LongSetter<SettableData> {
    private final int index;

    public LongSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void setLong(SettableData target, long value) throws Exception {
        target.setLong(index, value);
    }

    @Override
    public void set(SettableData target, Long value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setLong(index, value);
        }
    }
}
