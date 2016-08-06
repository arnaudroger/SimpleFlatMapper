package org.simpleflatmapper.datastax.impl.setter;

import com.datastax.driver.core.SettableByIndexData;
import org.simpleflatmapper.reflect.Setter;

import java.math.BigInteger;

public class BigIntegerSettableDataSetter implements Setter<SettableByIndexData, BigInteger> {
    private final int index;

    public BigIntegerSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(SettableByIndexData target, BigInteger value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setVarint(index, value);
        }
    }
}
