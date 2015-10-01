package org.sfm.datastax.impl.setter;

import com.datastax.driver.core.SettableData;
import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.IntSetter;

public class IntSettableDataSetter implements Setter<SettableData, Integer>, IntSetter<SettableData> {
    private final int index;

    public IntSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void setInt(SettableData target, int value) throws Exception {
        target.setInt(index, value);
    }

    @Override
    public void set(SettableData target, Integer value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setInt(index, value);
        }
    }
}
