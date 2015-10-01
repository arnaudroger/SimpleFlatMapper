package org.sfm.datastax.impl.setter;

import com.datastax.driver.core.SettableByIndexData;
import org.sfm.reflect.Setter;

import java.util.Date;

public class DateSettableDataSetter implements Setter<SettableByIndexData, Date> {
    private final int index;

    public DateSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(SettableByIndexData target, Date value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setDate(index, value);
        }
    }
}
