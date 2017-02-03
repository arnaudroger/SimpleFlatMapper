package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.FloatGetter;

public final class IndexedFloatArrayGetter implements Getter<float[], Float>, FloatGetter<float[]> {
    private final int index;

    public IndexedFloatArrayGetter(int index) {
        this.index = index;
    }

    @Override
    public float getFloat(float[] target) throws Exception {
        return target[index];
    }

    @Override
    public Float get(float[] target) throws Exception {
        return getFloat(target);
    }
}
