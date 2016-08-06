package org.simpleflatmapper.datastax.impl.setter;

import com.datastax.driver.core.SettableByIndexData;
import org.simpleflatmapper.reflect.Setter;

public class StringEnumSettableDataSetter implements Setter<SettableByIndexData, Enum<?>> {
    private final int index;

    public StringEnumSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(SettableByIndexData target, Enum<?> value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setString(index, value.name());
        }
    }
}
