package org.sfm.datastax.impl.setter;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.SettableData;
import org.sfm.reflect.Setter;

import java.util.Date;

public class OrdinalEnumSettableDataSetter implements Setter<SettableData, Enum<?>> {
    private final int index;

    public OrdinalEnumSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(SettableData target, Enum<?> value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setInt(index, value.ordinal());
        }
    }
}
