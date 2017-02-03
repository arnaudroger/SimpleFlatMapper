package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.BooleanGetter;

public final class IndexedBooleanArrayGetter implements Getter<boolean[], Boolean>, BooleanGetter<boolean[]> {
    private final int index;

    public IndexedBooleanArrayGetter(int index) {
        this.index = index;
    }

    @Override
    public boolean getBoolean(boolean[] target) throws Exception {
        return target[index];
    }

    @Override
    public Boolean get(boolean[] target) throws Exception {
        return getBoolean(target);
    }
}
