package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.DoubleGetter;

public final class IndexedDoubleArrayGetter implements Getter<double[], Double>, DoubleGetter<double[]> {
    private final int index;

    public IndexedDoubleArrayGetter(int index) {
        this.index = index;
    }

    @Override
    public double getDouble(double[] target) throws Exception {
        return target[index];
    }

    @Override
    public Double get(double[] target) throws Exception {
        return getDouble(target);
    }
}
