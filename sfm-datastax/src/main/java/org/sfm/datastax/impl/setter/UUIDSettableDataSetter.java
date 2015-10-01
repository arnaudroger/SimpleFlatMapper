package org.sfm.datastax.impl.setter;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.SettableData;
import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.FloatSetter;

import java.util.UUID;

public class UUIDSettableDataSetter implements Setter<SettableData, UUID> {
    private final int index;

    public UUIDSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(SettableData target, UUID value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setUUID(index, value);
        }
    }
}
