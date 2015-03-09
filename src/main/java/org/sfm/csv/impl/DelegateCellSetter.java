package org.sfm.csv.impl;

import org.sfm.reflect.Setter;
import org.sfm.utils.RowHandler;

public class DelegateCellSetter<T> implements CellSetter<T> {

	private final DelegateMarkerSetter<?> marker;
	private final CsvMapperCellConsumer handler;
    private final Setter setter;
	private final int cellIndex;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DelegateCellSetter(DelegateMarkerSetter<?> marker, int cellIndex, BreakDetector parentBreakDetector) {
		this.marker = marker;
		this.handler = ((CsvMapperImpl<?>)marker.getMapper()).newCellConsumer(null, parentBreakDetector);
        this.setter = marker.getSetter();
		this.cellIndex = cellIndex;
	}

	public DelegateCellSetter(DelegateMarkerSetter<?> marker,
                              CsvMapperCellConsumer handler,  int cellIndex) {
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
