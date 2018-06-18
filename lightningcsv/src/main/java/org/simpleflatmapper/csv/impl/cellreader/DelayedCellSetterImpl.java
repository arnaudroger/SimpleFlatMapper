package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.mapper.DelayedCellSetter;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.reflect.Setter;

public class DelayedCellSetterImpl<T, P> implements DelayedCellSetter<T, P> {

	private P value;

	private final CellValueReader<? extends P> reader;
	private final Setter<? super T, ? super P> setter;
	
	public DelayedCellSetterImpl(Setter<? super T, ? super P> setter, CellValueReader<? extends P> reader) {
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
		P val = consumeValue();
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
