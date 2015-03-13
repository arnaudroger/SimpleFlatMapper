package org.sfm.csv.impl;

import org.sfm.reflect.Setter;

public class DelegateDelayedCellSetterFactory<T, P> implements DelayedCellSetterFactory<T, P> {

	private final DelegateMarkerDelayedCellSetter<T, P> marker;
	private final CsvMapperCellConsumer<P> cellConsumer;
	private final int cellIndex;
    private final BreakDetector breakDetector;
    private final Setter<T, P> setter;


	public DelegateDelayedCellSetterFactory(DelegateMarkerDelayedCellSetter<T, P> marker, int cellIndex, BreakDetector breakDetector) {
		this.marker = marker;
		this.cellConsumer = ((CsvMapperImpl<P>)marker.getMapper()).newCellConsumer(null, breakDetector);
		this.cellIndex = cellIndex;
        this.setter = marker.getSetter();
        this.breakDetector = cellConsumer.getBreakDetector();
	}

	public DelegateDelayedCellSetterFactory(
			DelegateMarkerDelayedCellSetter<T, P> marker,
			CsvMapperCellConsumer<P> handler, int cellIndex) {
		this.cellConsumer = handler;
		this.marker = marker;
		this.cellIndex = cellIndex;
        this.setter = null;
        this.breakDetector = null;
	}

	public CsvMapperCellConsumer getCellHandler() {
		return cellConsumer;
	}

	@Override
	public DelayedCellSetter<T, P> newCellSetter() {
		return new DelegateDelayedCellSetter();
	}

    @Override
    public String toString() {
        return "DelegateDelayedCellSetterFactory{" +
                "cellIndex=" + cellIndex +
                ", marker=" + marker +
                ", handler=" + cellConsumer +
                '}';
    }

    public class DelegateDelayedCellSetter implements DelayedCellSetter<T, P> {

        public P getValue() {
            return cellConsumer.getCurrentInstance();
        }

        @Override
        public void set(T t) throws Exception {
            if (breakDetector == null
                    || (breakDetector.broken() && breakDetector.isNotNull())) {
                setter.set(t, getValue());
            }
        }

        @Override
        public boolean isSettable() {
            return setter != null;
        }

        public void set(char[] chars, int offset, int length, ParsingContext parsingContext)
                throws Exception {
            cellConsumer.newCell(chars, offset, length, cellIndex);
        }

        @Override
        public P consumeValue() {
            return getValue();
        }

        @Override
        public P peekValue() {
            return getValue();
        }
    }
}
