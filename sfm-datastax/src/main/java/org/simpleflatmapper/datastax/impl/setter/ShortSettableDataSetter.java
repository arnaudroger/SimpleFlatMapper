package org.simpleflatmapper.datastax.impl.setter;

import com.datastax.driver.core.SettableByIndexData;
import org.simpleflatmapper.datastax.DataHelper;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.ShortSetter;

public class ShortSettableDataSetter implements Setter<SettableByIndexData, Short>, ShortSetter<SettableByIndexData> {
    private final int index;

    public ShortSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void setShort(SettableByIndexData target, short value) throws Exception {
        DataHelper.setShort(index, value, target);
    }

    @Override
    public void set(SettableByIndexData target, Short value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            DataHelper.setShort(index, value, target);
        }
    }
}
