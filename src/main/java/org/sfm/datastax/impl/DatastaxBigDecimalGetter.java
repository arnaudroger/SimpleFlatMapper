package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableData;
import org.sfm.reflect.Getter;

import java.math.BigDecimal;

public class DatastaxBigDecimalGetter implements Getter<GettableData, BigDecimal> {

    private final int index;

    public DatastaxBigDecimalGetter(int index) {
        this.index = index;
    }

    @Override
    public BigDecimal get(GettableData target) throws Exception {
        return target.getDecimal(index);
    }
}
