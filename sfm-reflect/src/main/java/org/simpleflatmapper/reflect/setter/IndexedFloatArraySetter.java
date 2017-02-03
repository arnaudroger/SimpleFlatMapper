package org.simpleflatmapper.reflect.setter;

import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.FloatSetter;

public final class IndexedFloatArraySetter implements Setter<float[], Float>, FloatSetter<float[]> {
    private final int index;

    public IndexedFloatArraySetter(int index) {
        this.index = index;
    }

    @Override
    public void setFloat(float[] target, float value) throws Exception {
        target[index] = value;
    }

    @Override
    public void set(float[] target, Float value) throws Exception {
        setFloat(target, value);
    }
}
