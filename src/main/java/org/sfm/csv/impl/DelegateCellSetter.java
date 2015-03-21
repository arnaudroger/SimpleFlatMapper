package org.sfm.csv.impl;

import org.sfm.reflect.Setter;

public class DelegateCellSetter<T, P> implements CellSetter<T> {

	private final DelegateMarkerSetter<T, P> marker;
	private final CsvMapperCellConsumer<P> handler;
    private final Setter<T, P> setter;
	private final int cellIndex;

	@SuppressWarnings("unchecked")
    public DelegateCellSetter(DelegateMarkerSetter<T, P> marker, int cellIndex, BreakDetector parentBreakDetector) {
        if (marker== null) throw new NullPointerException("marker is null");
		this.marker = marker;
		this.handler = marker.getMapper().newCellConsumer(null, parentBreakDetector);
        this.setter = (Setter<T, P>) marker.getSetter();
		this.cellIndex = cellIndex;
	}

	public DelegateCellSetter(DelegateMarkerSetter<T, P> marker,
                              CsvMapperCellConsumer<P> handler,  int cellIndex) {
        if (handler== null) throw new NullPointerException("handler is null");
        if (marker== null) throw new NullPointerException("marker is null");
		this.marker = marker;
		this.handler = handler;
		this.cellIndex = cellIndex;
        this.setter = null;
	}

    @Override
	public void set(T target, char[] chars, int offset, int length, ParsingContext context)
			throws Exception {
		this.handler.newCell(chars, offset, length, cellIndex);
        final BreakDetector breakDetector = handler.getBreakDetector();
        if (setter != null && (breakDetector == null || (breakDetector.broken()&& breakDetector.isNotNull()))) {
            setter.set(target, this.handler.getCurrentInstance());
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
