package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.AbstractTargetSetters;
import org.sfm.csv.impl.TargetSetters;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.DoubleGetter;

public class DoubleDelayedGetter<T> implements DoubleGetter<AbstractTargetSetters<T>>, Getter<AbstractTargetSetters<T>, Double> {
	private final int index;
	
	public DoubleDelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public double getDouble(AbstractTargetSetters<T> target) throws Exception {
		return ((DoubleDelayedCellSetter<T>)target.getDelayedCellSetter(index)).consumeDouble();
	}

	@Override
	public Double get(AbstractTargetSetters<T> target) throws Exception {
		return getDouble(target);
	}

    @Override
    public String toString() {
        return "DoubleDelayedGetter{" +
                "index=" + index +
                '}';
    }
}
