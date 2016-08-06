package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.reflect.Getter;

import java.math.BigInteger;

public class DatastaxBigIntegerGetter implements Getter<GettableByIndexData, BigInteger> {

    private final int index;

    public DatastaxBigIntegerGetter(int index) {
        this.index = index;
    }

    @Override
    public BigInteger get(GettableByIndexData target) throws Exception {
        return target.getVarint(index);
    }
}
