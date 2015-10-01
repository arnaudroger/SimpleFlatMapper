package org.sfm.datastax.impl.setter;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.SettableData;
import org.sfm.reflect.Setter;

import java.util.Date;

public class DateSettableDataSetter implements Setter<SettableData, Date> {
    private final int index;

    public DateSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(SettableData target, Date value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setDate(index, value);
        }
    }
}
