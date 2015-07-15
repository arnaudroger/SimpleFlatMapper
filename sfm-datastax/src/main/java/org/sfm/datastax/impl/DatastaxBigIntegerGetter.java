package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableData;
import org.sfm.reflect.Getter;

import java.math.BigInteger;

public class DatastaxBigIntegerGetter implements Getter<GettableData, BigInteger> {

    private final int index;

    public DatastaxBigIntegerGetter(int index) {
        this.index = index;
    }

    @Override
    public BigInteger get(GettableData target) throws Exception {
        return target.getVarint(index);
    }
}
