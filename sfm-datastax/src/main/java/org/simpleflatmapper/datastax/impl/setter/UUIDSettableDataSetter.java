package org.simpleflatmapper.datastax.impl.setter;

import com.datastax.driver.core.SettableByIndexData;
import org.simpleflatmapper.reflect.Setter;
import java.util.UUID;

public class UUIDSettableDataSetter implements Setter<SettableByIndexData, UUID> {
    private final int index;

    public UUIDSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(SettableByIndexData target, UUID value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setUUID(index, value);
        }
    }
}
