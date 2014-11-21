package org.sfm.csv.impl;

import org.sfm.reflect.Setter;
import org.sfm.utils.RowHandler;

public class DelegateCellSetter<T> implements CellSetter<T> {

	private final DelegateMarkerSetter<?> marker;
	private final CsvMapperCellConsumer<?> handler;
	private T target;
	private final int cellIndex;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DelegateCellSetter(DelegateMarkerSetter<?> marker, int cellIndex) {
		this.marker = marker;
		this.handler = ((CsvMapperImpl<?>)marker.getMapper()).newCellConsumer(new RowHandler() {
			@Override
			public void handle(Object t) throws Exception {
				final Setter setter = DelegateCellSetter.this.marker.getSetter();
				setter.set(target, t);
			}

		});
		this.cellIndex = cellIndex;
	}

	public DelegateCellSetter(DelegateMarkerSetter<?> marker,
			CsvMapperCellConsumer<?> handler,  int cellIndex) {
		this.marker = marker;
		this.handler = handler;
		this.cellIndex = cellIndex;
	}

	@Override
	public void set(T target, char[] chars, int offset, int length, ParsingContext context)
			throws Exception {
		this.target = target;
		this.handler.newCell(chars, offset, length, cellIndex);
	}
	
	public CsvMapperCellConsumer<?> getBytesCellHandler() {
		return handler;
	}

}
