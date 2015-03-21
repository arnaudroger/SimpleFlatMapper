package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.CsvCellHandler;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.IntGetter;

public class IntDelayedGetter<T> implements IntGetter<CsvCellHandler<T>>, Getter<CsvCellHandler<T>, Integer> {
	private final int index;
	
	public IntDelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int getInt(CsvCellHandler<T> target) throws Exception {
		return ((IntDelayedCellSetter<T>)target.getDelayedCellSetter(index)).consumeInt();
	}

	@Override
	public Integer get(CsvCellHandler<T> target) throws Exception {
		return getInt(target);
	}

    @Override
    public String toString() {
        return "IntDelayedGetter{" +
                "index=" + index +
                '}';
    }
}
