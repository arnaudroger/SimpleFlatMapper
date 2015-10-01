package org.sfm.datastax.impl.setter;

import com.datastax.driver.core.SettableByIndexData;
import com.datastax.driver.core.TupleValue;
import org.sfm.reflect.Setter;

public class TupleValueSettableDataSetter implements Setter<SettableByIndexData, TupleValue> {
    private final int index;

    public TupleValueSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(SettableByIndexData target, TupleValue value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setTupleValue(index, value);
        }
    }
}
