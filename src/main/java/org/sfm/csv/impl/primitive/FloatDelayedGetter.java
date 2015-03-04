package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.DelayedCellSetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.FloatGetter;

public class FloatDelayedGetter<T> implements FloatGetter<DelayedCellSetter<T, ?>[]>, Getter<DelayedCellSetter<T, ?>[], Float> {
	private final int index;
	
	public FloatDelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public float getFloat(DelayedCellSetter<T, ?>[] target) throws Exception {
		return ((FloatDelayedCellSetter<T>)target[index]).consumeFloat();
	}

	@Override
	public Float get(DelayedCellSetter<T, ?>[] target) throws Exception {
		return getFloat(target);
	}

    @Override
    public String toString() {
        return "FloatDelayedGetter{" +
                "index=" + index +
                '}';
    }
}
