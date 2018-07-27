package org.simpleflatmapper.datastax.impl.setter;

import com.datastax.driver.core.LocalDate;
import com.datastax.driver.core.SettableByIndexData;
import org.simpleflatmapper.reflect.Setter;

public class DateSettableDataSetter implements Setter<SettableByIndexData, LocalDate>{
    private final int index;

    public DateSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(SettableByIndexData target, LocalDate value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setDate(index, value);
        }
    }
}
