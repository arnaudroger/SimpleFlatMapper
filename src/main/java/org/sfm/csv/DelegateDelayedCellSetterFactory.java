package org.sfm.csv;

import org.sfm.utils.RowHandler;

public class DelegateDelayedCellSetterFactory<T, P> implements DelayedCellSetterFactory<T, P> {

	private final DelegateMarkerDelayedCellSetter<T, P> marker;
	private final CsvMapperCellHandler<?> handler;
	private final int cellIndex;
	protected P value;
	public DelegateDelayedCellSetterFactory(DelegateMarkerDelayedCellSetter<T, P> marker, int cellIndex) {
		this.marker = marker;
		this.handler = ((CsvMapperImpl<P>)marker.getMapper()).newCellHandler(new RowHandler<P>() {
			@Override
			public void handle(P t) throws Exception {
				DelegateDelayedCellSetterFactory.this.value = t;
			}
			
		});
		this.cellIndex = cellIndex;
	}

	public DelegateDelayedCellSetterFactory(
			DelegateMarkerDelayedCellSetter<T, P> marker,
			CsvMapperCellHandler<?> bhandler, int cellIndex) {
		this.handler = bhandler;
		this.marker = marker;
		this.cellIndex = cellIndex;
	}

	public CsvMapperCellHandler<?> getBytesCellHandler() {
		return handler;
	}

	@Override
	public DelayedCellSetter<T, P> newCellSetter() {
		return new DelayedCellSetter<T, P>() {

			@Override
			public P getValue() {
				return value;
			}

			@Override
			public void set(T t) throws Exception {
				marker.getSetter().set(t, value);
				
			}

			@Override
			public boolean isSettable() {
				return marker.getSetter() != null;
			}
			
			public void set(char[] chars, int offset, int length)
					throws Exception {
				handler.newCell(chars, offset, length, cellIndex);

			}
		};
	}

}
