package org.simpleflatmapper.datastax.impl.setter;

import com.datastax.driver.core.SettableByIndexData;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.LongSetter;

public class LongSettableDataSetter implements Setter<SettableByIndexData, Long>, LongSetter<SettableByIndexData> {
    private final int index;

    public LongSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void setLong(SettableByIndexData target, long value) throws Exception {
        target.setLong(index, value);
    }

    @Override
    public void set(SettableByIndexData target, Long value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setLong(index, value);
        }
    }
}
