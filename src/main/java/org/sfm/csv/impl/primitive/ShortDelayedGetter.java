package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.AbstractTargetSetters;
import org.sfm.csv.impl.TargetSetters;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.ShortGetter;

public class ShortDelayedGetter<T> implements ShortGetter<AbstractTargetSetters<T>>, Getter<AbstractTargetSetters<T>, Short> {
	private final int index;
	
	public ShortDelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public short getShort(AbstractTargetSetters<T> target) throws Exception {
		return ((ShortDelayedCellSetter<T>)target.getDelayedCellSetter(index)).getShort();
	}

	@Override
	public Short get(AbstractTargetSetters<T> target) throws Exception {
		return getShort(target);
	}

    @Override
    public String toString() {
        return "ShortDelayedGetter{" +
                "index=" + index +
                '}';
    }
}
