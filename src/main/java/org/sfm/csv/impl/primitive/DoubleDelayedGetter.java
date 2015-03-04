package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.DelayedCellSetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.DoubleGetter;

public class DoubleDelayedGetter<T> implements DoubleGetter<DelayedCellSetter<T, ?>[]>, Getter<DelayedCellSetter<T, ?>[], Double> {
	private final int index;
	
	public DoubleDelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public double getDouble(DelayedCellSetter<T, ?>[] target) throws Exception {
		return ((DoubleDelayedCellSetter<T>)target[index]).consumeDouble();
	}

	@Override
	public Double get(DelayedCellSetter<T, ?>[] target) throws Exception {
		return getDouble(target);
	}

    @Override
    public String toString() {
        return "DoubleDelayedGetter{" +
                "index=" + index +
                '}';
    }
}
