package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.CsvMapperCellHandler;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.BooleanGetter;

public class BooleanDelayedGetter<T> implements BooleanGetter<CsvMapperCellHandler<T>>, Getter<CsvMapperCellHandler<T>, Boolean> {
	private final int index;
	
	public BooleanDelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean getBoolean(CsvMapperCellHandler<T> target) throws Exception {
		return ((BooleanDelayedCellSetter<T>)target.getDelayedCellSetter(index)).consumeBoolean();
	}

	@Override
	public Boolean get(CsvMapperCellHandler<T> target) throws Exception {
		return getBoolean(target);
	}

    @Override
    public String toString() {
        return "BooleanDelayedGetter{" +
                "index=" + index +
                '}';
    }
}
