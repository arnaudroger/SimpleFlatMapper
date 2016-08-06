package org.simpleflatmapper.datastax.impl.setter;

import com.datastax.driver.core.SettableByIndexData;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.IntSetter;

public class IntSettableDataSetter implements Setter<SettableByIndexData, Integer>, IntSetter<SettableByIndexData> {
    private final int index;

    public IntSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void setInt(SettableByIndexData target, int value) throws Exception {
        target.setInt(index, value);
    }

    @Override
    public void set(SettableByIndexData target, Integer value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setInt(index, value);
        }
    }
}
