package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.DelayedCellSetter;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.reflect.Setter;

public class DelayedCellSetterImpl<T, P> implements DelayedCellSetter<T, P> {

	private P value;

	private final CellValueReader<? extends P> reader;
	private final Setter<T, ? super P> setter;
	
	public DelayedCellSetterImpl(Setter<T, ? super P> setter, CellValueReader<? extends P> reader) {
		this.setter = setter;
		this.reader = reader;
	}

	@Override
	public P consumeValue() {
		P val = value;
		value = null;
		return val;
	}

    @Override
    public P peekValue() {
        return value;
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
	public void set(char[] chars, int offset, int length, ParsingContext parsingContext)
			throws Exception {
		value = reader.read(chars, offset, length, parsingContext);
	}

    @Override
    public String toString() {
        return "DelayedCellSetterImpl{" +
                "reader=" + reader +
                ", setter=" + setter +
                '}';
    }
}
