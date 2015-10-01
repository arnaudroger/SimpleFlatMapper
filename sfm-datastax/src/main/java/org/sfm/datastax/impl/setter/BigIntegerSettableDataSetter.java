package org.sfm.datastax.impl.setter;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.SettableData;
import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.FloatSetter;

import java.math.BigInteger;

public class BigIntegerSettableDataSetter implements Setter<SettableData, BigInteger> {
    private final int index;

    public BigIntegerSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(SettableData target, BigInteger value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setVarint(index, value);
        }
    }
}
