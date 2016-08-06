package org.simpleflatmapper.datastax.impl.setter;

import com.datastax.driver.core.SettableByIndexData;
import org.simpleflatmapper.reflect.Setter;

public class StringSettableDataSetter implements Setter<SettableByIndexData, String> {
    private final int index;

    public StringSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(SettableByIndexData target, String value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setString(index, value);
        }
    }
}
