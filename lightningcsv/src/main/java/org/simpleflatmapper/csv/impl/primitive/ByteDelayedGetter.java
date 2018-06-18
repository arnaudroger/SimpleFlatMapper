package org.simpleflatmapper.csv.impl.primitive;

import org.simpleflatmapper.csv.mapper.CsvMapperCellHandler;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.ByteGetter;

public class ByteDelayedGetter<T> implements ByteGetter<CsvMapperCellHandler<T>>, Getter<CsvMapperCellHandler<T>, Byte> {
	private final int index;
	
	public ByteDelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public byte getByte(CsvMapperCellHandler<T> target) throws Exception {
		return ((ByteDelayedCellSetter<T>)target.getDelayedCellSetter(index)).consumeByte();
	}

	@Override
	public Byte get(CsvMapperCellHandler<T> target) throws Exception {
		return getByte(target);
	}

    @Override
    public String toString() {
        return "ByteDelayedGetter{" +
                "index=" + index +
                '}';
    }
}
