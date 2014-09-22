package org.sfm.csv;

import org.sfm.reflect.Setter;
import org.sfm.utils.RowHandler;

public class DelegateCellSetter<T> implements CellSetter<T> {

	private final DelegateMarkerSetter<?> marker;
	private final CsvMapperBytesCellHandler<?> handler;
	private T target;
	private final int cellIndex;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DelegateCellSetter(DelegateMarkerSetter<?> marker, int cellIndex) {
		this.marker = marker;
		this.handler = ((CsvMapperImpl<?>)marker.getMapper()).newCellHandler(new RowHandler() {
			@Override
			public void handle(Object t) throws Exception {
				final Setter setter = DelegateCellSetter.this.marker.getSetter();
				setter.set(target, t);
			}
			
		});
		this.cellIndex = cellIndex;
	}

	public DelegateCellSetter(DelegateMarkerSetter<?> marker,
			CsvMapperBytesCellHandler<?> handler,  int cellIndex) {
		this.marker = marker;
		this.handler = handler;
		this.cellIndex = cellIndex;
	}

	@Override
	public void set(T target, byte[] bytes, int offset, int length)
			throws Exception {
		this.target = target;
		this.handler.newCell(bytes, offset, length, cellIndex);
	}

	public CsvMapperBytesCellHandler<?> getBytesCellHandler() {
		return handler;
	}

}
