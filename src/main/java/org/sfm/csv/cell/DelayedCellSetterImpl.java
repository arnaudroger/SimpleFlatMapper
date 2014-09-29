package org.sfm.csv.cell;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.DecoderContext;
import org.sfm.csv.DelayedCellSetter;
import org.sfm.reflect.Setter;

public class DelayedCellSetterImpl<T, P> implements DelayedCellSetter<T, P> {

	private P value;

	private final CellValueReader<P> reader;
	private final Setter<T, P> setter;
	
	public DelayedCellSetterImpl(Setter<T, P> setter, CellValueReader<P> reader) {
		this.setter = setter;
		this.reader = reader;
	}

	@Override
	public P getValue() {
		P val = value;
		value = null;
		return val;
	}

	@Override
	public void set(T t) throws Exception {
		P val = value;
		value = null;
		setter.set(t, val);
	}

	@Override
	public boolean isSettable() {
		return setter != null;
	}
	
	@Override
	public void set(byte[] bytes, int offset, int length, DecoderContext decoderContext)
			throws Exception {
		value = reader.read(bytes, offset, length, decoderContext);
	}

	@Override
	public void set(char[] chars, int offset, int length)
			throws Exception {
		value = reader.read(chars, offset, length);
	}

}
