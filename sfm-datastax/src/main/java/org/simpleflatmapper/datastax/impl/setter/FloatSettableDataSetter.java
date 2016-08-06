package org.simpleflatmapper.datastax.impl.setter;

import com.datastax.driver.core.SettableByIndexData;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.FloatSetter;

public class FloatSettableDataSetter implements Setter<SettableByIndexData, Float>, FloatSetter<SettableByIndexData> {
    private final int index;

    public FloatSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void setFloat(SettableByIndexData target, float value) throws Exception {
        target.setFloat(index, value);
    }

    @Override
    public void set(SettableByIndexData target, Float value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setFloat(index, value);
        }
    }
}
