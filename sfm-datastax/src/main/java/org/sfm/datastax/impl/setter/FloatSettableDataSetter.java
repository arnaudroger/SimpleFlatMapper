package org.sfm.datastax.impl.setter;

import com.datastax.driver.core.SettableData;
import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.FloatSetter;

public class FloatSettableDataSetter implements Setter<SettableData, Float>, FloatSetter<SettableData> {
    private final int index;

    public FloatSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void setFloat(SettableData target, float value) throws Exception {
        target.setFloat(index, value);
    }

    @Override
    public void set(SettableData target, Float value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setFloat(index, value);
        }
    }
}
