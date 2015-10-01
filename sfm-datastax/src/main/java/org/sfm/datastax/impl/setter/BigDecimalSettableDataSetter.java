package org.sfm.datastax.impl.setter;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.SettableData;
import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.FloatSetter;

import java.math.BigDecimal;

public class BigDecimalSettableDataSetter implements Setter<SettableData, BigDecimal> {
    private final int index;

    public BigDecimalSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(SettableData target, BigDecimal value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setDecimal(index, value);
        }
    }
}
