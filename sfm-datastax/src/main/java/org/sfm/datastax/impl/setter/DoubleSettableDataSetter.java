package org.sfm.datastax.impl.setter;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.SettableData;
import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.DoubleSetter;

public class DoubleSettableDataSetter implements Setter<SettableData, Double>, DoubleSetter<SettableData> {
    private final int index;

    public DoubleSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void setDouble(SettableData target, double value) throws Exception {
        target.setDouble(index, value);
    }

    @Override
    public void set(SettableData target, Double value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setDouble(index, value);
        }
    }
}
