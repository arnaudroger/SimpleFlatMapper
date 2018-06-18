package org.simpleflatmapper.csv.impl;

import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.mapper.BreakDetector;
import org.simpleflatmapper.csv.mapper.CsvMapperCellConsumer;
import org.simpleflatmapper.csv.mapper.DelayedCellSetter;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.setter.AppendCollectionSetter;
import org.simpleflatmapper.reflect.setter.NullSetter;

public class DelegateDelayedCellSetter<T, P> implements DelayedCellSetter<T, P> {

	private final CsvMapperCellConsumer<P> cellConsumer;
	private final int cellIndex;
    private final BreakDetector breakDetector;
    private final Setter<? super T, ? super P> setter;


	public DelegateDelayedCellSetter(DelegateMarkerDelayedCellSetterFactory<T, P> marker, int cellIndex, BreakDetector breakDetector) {
		this.cellConsumer = ((CsvMapperImpl<P>)marker.getMapper()).newCellConsumer(null, breakDetector, marker.getSetter() instanceof AppendCollectionSetter);
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
