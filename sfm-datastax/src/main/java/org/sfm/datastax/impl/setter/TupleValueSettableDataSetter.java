package org.sfm.datastax.impl.setter;

import com.datastax.driver.core.SettableData;
import com.datastax.driver.core.TupleValue;
import org.sfm.reflect.Setter;

import java.util.Date;

public class TupleValueSettableDataSetter implements Setter<SettableData, TupleValue> {
    private final int index;

    public TupleValueSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(SettableData target, TupleValue value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setTupleValue(index, value);
        }
    }
}
