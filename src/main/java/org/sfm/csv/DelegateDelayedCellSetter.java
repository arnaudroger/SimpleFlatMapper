package org.sfm.csv;

import org.sfm.utils.RowHandler;

public class DelegateDelayedCellSetter<T, P> implements DelayedCellSetter<T, P> {

	private final DelegateMarkerDelayedCellSetter<T, P> marker;
	private final CsvMapperBytesCellHandler<?> handler;
	private final int cellIndex;
	protected P value;
	public DelegateDelayedCellSetter(
			DelegateMarkerDelayedCellSetter<T, P> marker, int cellIndex) {
		this.marker = marker;
		this.handler = ((CsvMapperImpl<P>)marker.getMapper()).newCellHandler(new RowHandler<P>() {
			@Override
			public void handle(P t) throws Exception {
				DelegateDelayedCellSetter.this.value = t;
			}
			
		});
		this.cellIndex = cellIndex;
	}

	public DelegateDelayedCellSetter(
			DelegateMarkerDelayedCellSetter<T, P> marker,
			CsvMapperBytesCellHandler<?> bhandler, int cellIndex) {
		this.handler = bhandler;
		this.marker = marker;
		this.cellIndex = cellIndex;
	}

	public CsvMapperBytesCellHandler<?> getBytesCellHandler() {
		return handler;
	}

	@Override
	public DelayedSetter<T, P> set(byte[] bytes, int offset, int length)
			throws Exception {
		handler.newCell(bytes, offset, length, cellIndex);
		return new DelayedSetter<T, P>() {

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
		};
	}

}
