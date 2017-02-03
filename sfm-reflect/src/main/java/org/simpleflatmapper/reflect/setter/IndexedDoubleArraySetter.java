package org.simpleflatmapper.reflect.setter;

import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.DoubleSetter;

public final class IndexedDoubleArraySetter implements Setter<double[], Double>, DoubleSetter<double[]> {
    private final int index;

    public IndexedDoubleArraySetter(int index) {
        this.index = index;
    }

    @Override
    public void setDouble(double[] target, double value) throws Exception {
        target[index] = value;
    }

    @Override
    public void set(double[] target, Double value) throws Exception {
        setDouble(target, value);
    }
}
