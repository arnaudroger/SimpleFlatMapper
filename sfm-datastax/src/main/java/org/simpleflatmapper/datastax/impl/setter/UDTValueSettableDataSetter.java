package org.simpleflatmapper.datastax.impl.setter;

import com.datastax.driver.core.SettableByIndexData;
import com.datastax.driver.core.UDTValue;
import org.simpleflatmapper.reflect.Setter;

public class UDTValueSettableDataSetter implements Setter<SettableByIndexData, UDTValue> {
    private final int index;

    public UDTValueSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(SettableByIndexData target, UDTValue value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setUDTValue(index, value);
        }
    }
}
