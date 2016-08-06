package org.simpleflatmapper.datastax.impl.setter;

import com.datastax.driver.core.SettableByIndexData;
import org.simpleflatmapper.reflect.Setter;

import java.math.BigDecimal;

public class BigDecimalSettableDataSetter implements Setter<SettableByIndexData, BigDecimal> {
    private final int index;

    public BigDecimalSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(SettableByIndexData target, BigDecimal value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setDecimal(index, value);
        }
    }
}
