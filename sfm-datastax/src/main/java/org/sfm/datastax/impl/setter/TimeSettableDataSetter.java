package org.sfm.datastax.impl.setter;

import com.datastax.driver.core.SettableByIndexData;
import org.sfm.datastax.DataHelper;
import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.LongSetter;
import org.sfm.reflect.primitive.ShortSetter;

public class TimeSettableDataSetter implements Setter<SettableByIndexData, Long>, LongSetter<SettableByIndexData> {
    private final int index;

    public TimeSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void setLong(SettableByIndexData target, long value) throws Exception {
        DataHelper.setTime(index, value, target);
    }

    @Override
    public void set(SettableByIndexData target, Long value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            DataHelper.setTime(index, value, target);
        }
    }
}
