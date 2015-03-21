package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.CsvCellHandler;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.LongGetter;

public class LongDelayedGetter<T> implements LongGetter<CsvCellHandler<T>>, Getter<CsvCellHandler<T>, Long> {
	private final int index;
	
	public LongDelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public long getLong(CsvCellHandler<T> target) throws Exception {
		return ((LongDelayedCellSetter<T>)target.getDelayedCellSetter(index)).consumeLong();
	}

	@Override
	public Long get(CsvCellHandler<T> target) throws Exception {
		return getLong(target);
	}

    @Override
    public String toString() {
        return "LongDelayedGetter{" +
                "index=" + index +
                '}';
    }
}
