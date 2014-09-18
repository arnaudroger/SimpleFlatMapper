package org.sfm.csv;

import org.sfm.reflect.Setter;

public final class CellSetterImpl<T, P> implements CellSetter<T> {

	private final CellValueTransfomer<P> cellValueTransformer;
	private final Setter<T, P> setter;

	public CellSetterImpl(CellValueTransfomer<P> cellValueTransformer,
			Setter<T, P> setter) {
		this.cellValueTransformer = cellValueTransformer;
		this.setter = setter;
	}

	@Override
	public void set(T target, byte[] bytes, int offset, int length) throws Exception {
		setter.set(target, cellValueTransformer.transform(bytes, offset, length));
	}

}
