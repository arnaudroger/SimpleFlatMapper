package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.AbstractTargetSetters;
import org.sfm.csv.impl.TargetSetters;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.BooleanGetter;

public class BooleanDelayedGetter<T> implements BooleanGetter<AbstractTargetSetters<T>>, Getter<AbstractTargetSetters<T>, Boolean> {
	private final int index;
	
	public BooleanDelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean getBoolean(AbstractTargetSetters<T> target) throws Exception {
		return ((BooleanDelayedCellSetter<T>)target.getDelayedCellSetter(index)).consumeBoolean();
	}

	@Override
	public Boolean get(AbstractTargetSetters<T> target) throws Exception {
		return getBoolean(target);
	}

    @Override
    public String toString() {
        return "BooleanDelayedGetter{" +
                "index=" + index +
                '}';
    }
}
