package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.CsvMapperCellHandler;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.IntGetter;

public class IntDelayedGetter<T> implements IntGetter<CsvMapperCellHandler<T>>, Getter<CsvMapperCellHandler<T>, Integer> {
	private final int index;
	
	public IntDelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int getInt(CsvMapperCellHandler<T> target) throws Exception {
		return ((IntDelayedCellSetter<T>)target.getDelayedCellSetter(index)).consumeInt();
	}

	@Override
	public Integer get(CsvMapperCellHandler<T> target) throws Exception {
		return getInt(target);
	}

    @Override
    public String toString() {
        return "IntDelayedGetter{" +
                "index=" + index +
                '}';
    }
}
