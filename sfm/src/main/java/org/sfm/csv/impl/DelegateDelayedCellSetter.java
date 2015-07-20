package org.sfm.csv.impl;

import org.sfm.csv.ParsingContext;
import org.sfm.csv.mapper.BreakDetector;
import org.sfm.csv.mapper.CsvMapperCellConsumer;
import org.sfm.csv.mapper.DelayedCellSetter;
import org.sfm.reflect.Setter;

public class DelegateDelayedCellSetter<T, P> implements DelayedCellSetter<T, P> {

	private final CsvMapperCellConsumer<P> cellConsumer;
	private final int cellIndex;
    private final BreakDetector breakDetector;
    private final Setter<T, P> setter;


	public DelegateDelayedCellSetter(DelegateMarkerDelayedCellSetterFactory<T, P> marker, int cellIndex, BreakDetector breakDetector) {
		this.cellConsumer = ((CsvMapperImpl<P>)marker.getMapper()).newCellConsumer(null, breakDetector);
		this.cellIndex = cellIndex;
        this.setter = marker.getSetter();
        this.breakDetector = cellConsumer.getBreakDetector();
	}

	public DelegateDelayedCellSetter(CsvMapperCellConsumer<P> handler, int cellIndex) {
		this.cellConsumer = handler;
		this.cellIndex = cellIndex;
        this.setter = null;
        this.breakDetector = null;
	}

	public CsvMapperCellConsumer getCellHandler() {
		return cellConsumer;
	}


    @Override
    public String toString() {
        return "DelegateDelayedCellSetterFactory{" +
                "cellIndex=" + cellIndex +
                ", handler=" + cellConsumer +
                '}';
    }


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
