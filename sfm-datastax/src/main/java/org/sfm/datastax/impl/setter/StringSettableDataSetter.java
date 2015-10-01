package org.sfm.datastax.impl.setter;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.SettableData;
import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.LongSetter;

public class StringSettableDataSetter implements Setter<SettableData, String> {
    private final int index;

    public StringSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(SettableData target, String value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setString(index, value);
        }
    }
}
