package org.simpleflatmapper.reflect.setter;

import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.BooleanSetter;

public final class IndexedBooleanArraySetter implements Setter<boolean[], Boolean>, BooleanSetter<boolean[]> {
    private final int index;

    public IndexedBooleanArraySetter(int index) {
        this.index = index;
    }

    @Override
    public void setBoolean(boolean[] target, boolean value) throws Exception {
        target[index] = value;
    }

    @Override
    public void set(boolean[] target, Boolean value) throws Exception {
        setBoolean(target, value);
    }
}
