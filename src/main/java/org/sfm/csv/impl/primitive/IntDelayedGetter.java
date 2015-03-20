package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.AbstractTargetSetters;
import org.sfm.csv.impl.TargetSetters;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.IntGetter;

public class IntDelayedGetter<T> implements IntGetter<AbstractTargetSetters<T>>, Getter<AbstractTargetSetters<T>, Integer> {
	private final int index;
	
	public IntDelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int getInt(AbstractTargetSetters<T> target) throws Exception {
		return ((IntDelayedCellSetter<T>)target.getDelayedCellSetter(index)).consumeInt();
	}

	@Override
	public Integer get(AbstractTargetSetters<T> target) throws Exception {
		return getInt(target);
	}

    @Override
    public String toString() {
        return "IntDelayedGetter{" +
                "index=" + index +
                '}';
    }
}
