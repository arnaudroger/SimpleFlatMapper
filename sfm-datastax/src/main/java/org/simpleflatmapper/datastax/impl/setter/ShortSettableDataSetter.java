package org.simpleflatmapper.datastax.impl.setter;

import com.datastax.driver.core.SettableByIndexData;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.ShortSetter;

public class ShortSettableDataSetter implements Setter<SettableByIndexData, Short>, ShortSetter<SettableByIndexData> {
    private final int index;

    public ShortSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void setShort(SettableByIndexData target, short value) throws Exception {
        target.setShort(index, value);
    }

    @Override
    public void set(SettableByIndexData target, Short value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            setShort(target, value);
        }
    }
}
