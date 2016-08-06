package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.reflect.Getter;

import java.math.BigDecimal;

public class DatastaxBigDecimalGetter implements Getter<GettableByIndexData, BigDecimal> {

    private final int index;

    public DatastaxBigDecimalGetter(int index) {
        this.index = index;
    }

    @Override
    public BigDecimal get(GettableByIndexData target) throws Exception {
        return target.getDecimal(index);
    }
}
