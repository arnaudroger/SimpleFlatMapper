package org.sfm.csv.impl;

import org.sfm.utils.RowHandler;

public class DelegateDelayedCellSetterFactory<T, P> implements DelayedCellSetterFactory<T, P> {

	private final DelegateMarkerDelayedCellSetter<T, P> marker;
	private final CsvMapperCellConsumer<?> handler;
	private final int cellIndex;
	protected P value;
	public DelegateDelayedCellSetterFactory(DelegateMarkerDelayedCellSetter<T, P> marker, int cellIndex) {
		this.marker = marker;
		this.handler = ((CsvMapperImpl<P>)marker.getMapper()).newCellConsumer(new RowHandler<P>() {
			@Override
			public void handle(P t) throws Exception {
				DelegateDelayedCellSetterFactory.this.value = t;
			}

		});
		this.cellIndex = cellIndex;
	}

	public DelegateDelayedCellSetterFactory(
			DelegateMarkerDelayedCellSetter<T, P> marker,
			CsvMapperCellConsumer<?> handler, int cellIndex) {
		this.handler = handler;
		this.marker = marker;
		this.cellIndex = cellIndex;
	}

	public CsvMapperCellConsumer<?> getCellHandler() {
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
			
			public void set(char[] chars, int offset, int length, ParsingContext parsingContext)
					throws Exception {
				handler.newCell(chars, offset, length, cellIndex);

			}
		};
	}

    @Override
    public String toString() {
        return "DelegateDelayedCellSetterFactory{" +
                "cellIndex=" + cellIndex +
                ", marker=" + marker +
                ", handler=" + handler +
                '}';
    }
}
