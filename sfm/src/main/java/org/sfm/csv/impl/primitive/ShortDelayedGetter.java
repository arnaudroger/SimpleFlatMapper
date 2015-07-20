package org.sfm.csv.impl.primitive;

import org.sfm.csv.mapper.CsvMapperCellHandler;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.ShortGetter;

public class ShortDelayedGetter<T> implements ShortGetter<CsvMapperCellHandler<T>>, Getter<CsvMapperCellHandler<T>, Short> {
	private final int index;
	
	public ShortDelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public short getShort(CsvMapperCellHandler<T> target) throws Exception {
		return ((ShortDelayedCellSetter<T>)target.getDelayedCellSetter(index)).getShort();
	}

	@Override
	public Short get(CsvMapperCellHandler<T> target) throws Exception {
		return getShort(target);
	}

    @Override
    public String toString() {
        return "ShortDelayedGetter{" +
                "index=" + index +
                '}';
    }
}
