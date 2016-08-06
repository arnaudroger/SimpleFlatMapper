package org.simpleflatmapper.datastax.impl.setter;

import com.datastax.driver.core.SettableByIndexData;
import org.simpleflatmapper.datastax.DataHelper;
import org.simpleflatmapper.reflect.Setter;

public class DateSettableDataSetter implements Setter<SettableByIndexData, Object>{
    private final int index;

    public DateSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(SettableByIndexData target, Object value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            DataHelper.setDate(index, value, target);
        }
    }
}
