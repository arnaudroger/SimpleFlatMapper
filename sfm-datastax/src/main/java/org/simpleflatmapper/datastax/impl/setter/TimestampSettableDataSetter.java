package org.simpleflatmapper.datastax.impl.setter;

import com.datastax.driver.core.SettableByIndexData;
import org.simpleflatmapper.reflect.Setter;

import java.util.Date;

public class TimestampSettableDataSetter implements Setter<SettableByIndexData, Date> {
    private final int index;

    public TimestampSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(SettableByIndexData target, Date value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setTimestamp(index, value);
        }
    }
}
