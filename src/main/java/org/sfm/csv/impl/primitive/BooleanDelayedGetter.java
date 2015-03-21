package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.CsvCellHandler;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.BooleanGetter;

public class BooleanDelayedGetter<T> implements BooleanGetter<CsvCellHandler<T>>, Getter<CsvCellHandler<T>, Boolean> {
	private final int index;
	
	public BooleanDelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean getBoolean(CsvCellHandler<T> target) throws Exception {
		return ((BooleanDelayedCellSetter<T>)target.getDelayedCellSetter(index)).consumeBoolean();
	}

	@Override
	public Boolean get(CsvCellHandler<T> target) throws Exception {
		return getBoolean(target);
	}

    @Override
    public String toString() {
        return "BooleanDelayedGetter{" +
                "index=" + index +
                '}';
    }
}
