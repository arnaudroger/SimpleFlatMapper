package org.simpleflatmapper.csv.impl;

import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.mapper.BreakDetector;
import org.simpleflatmapper.csv.mapper.CellSetter;
import org.simpleflatmapper.csv.mapper.CsvMapperCellConsumer;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.setter.AppendCollectionSetter;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public class DelegateCellSetter<T, P> implements CellSetter<T> {

	private final DelegateMarkerSetter<T, P> marker;
	private final CsvMapperCellConsumer<P> handler;
    private final Setter<? super T, ? super P> setter;
	private final int cellIndex;

	@SuppressWarnings("unchecked")
    public DelegateCellSetter(DelegateMarkerSetter<T, P> marker, int cellIndex, BreakDetector parentBreakDetector) {
		this.marker = requireNonNull("marker",  marker);
		this.handler = marker.getMapper().newCellConsumer(null, parentBreakDetector, marker.getSetter() instanceof AppendCollectionSetter);
        this.setter = marker.getSetter();
		this.cellIndex = cellIndex;
	}

	public DelegateCellSetter(DelegateMarkerSetter<T, P> marker,
                              CsvMapperCellConsumer<P> handler,  int cellIndex) {
		this.marker = requireNonNull("marker", marker);
		this.handler = requireNonNull("handler",handler);
		this.cellIndex = cellIndex;
        this.setter = null;
	}

    @Override
	public void set(T target, char[] chars, int offset, int length, ParsingContext context)
			throws Exception {
		this.handler.newCell(chars, offset, length, cellIndex);
        final BreakDetector breakDetector = handler.getBreakDetector();
        if (setter != null && (breakDetector == null || (breakDetector.broken()&& breakDetector.isNotNull()))) {
            setter.set(target, this.handler.getOrCreateCurrentInstance());
        }
	}

	public CsvMapperCellConsumer getCellConsumer() {
		return handler;
	}

    @Override
    public String toString() {
        return "DelegateCellSetter{" +
                "marker=" + marker +
                ", handler=" + handler +
                ", cellIndex=" + cellIndex +
                '}';
    }
}
