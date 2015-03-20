package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.AbstractTargetSetters;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.FloatGetter;

public class FloatDelayedGetter<T> implements FloatGetter<AbstractTargetSetters<T>>, Getter<AbstractTargetSetters<T>, Float> {
	private final int index;
	
	public FloatDelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public float getFloat(AbstractTargetSetters<T> target) throws Exception {
		return ((FloatDelayedCellSetter<T>)target.getDelayedCellSetter(index)).consumeFloat();
	}

	@Override
	public Float get(AbstractTargetSetters<T> target) throws Exception {
		return getFloat(target);
	}

    @Override
    public String toString() {
        return "FloatDelayedGetter{" +
                "index=" + index +
                '}';
    }
}
