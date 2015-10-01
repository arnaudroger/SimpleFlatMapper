package org.sfm.datastax.impl.setter;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.SettableData;
import org.sfm.reflect.Setter;

import java.util.Date;

public class StringEnumSettableDataSetter implements Setter<SettableData, Enum<?>> {
    private final int index;

    public StringEnumSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(SettableData target, Enum<?> value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setString(index, value.name());
        }
    }
}
