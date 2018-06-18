package org.simpleflatmapper.csv.impl.primitive;

import org.simpleflatmapper.csv.mapper.CsvMapperCellHandler;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.ShortGetter;

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
