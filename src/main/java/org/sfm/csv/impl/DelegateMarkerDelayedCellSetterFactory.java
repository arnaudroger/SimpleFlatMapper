package org.sfm.csv.impl;

import org.sfm.csv.CsvMapper;
import org.sfm.reflect.Setter;

import java.util.Map;

public class DelegateMarkerDelayedCellSetterFactory<T, P> implements DelayedCellSetterFactory<T, P> {

	private final CsvMapper<P> mapper;
	private final Setter<T, P> setter;
    private final int index;
	private final int parent;

	public DelegateMarkerDelayedCellSetterFactory(CsvMapper<P> mapper, Setter<T, P> setter, int index, int parent) {
		this.mapper = mapper;
		this.setter = setter;
        this.index = index;
        this.parent = parent;
    }

	public CsvMapper<P> getMapper() {
		return mapper;
	}

	public Setter<T, P> getSetter() {
		return setter;
	}

	@Override
	public DelayedCellSetter<T, P> newCellSetter(BreakDetector breakDetector, CsvMapperCellConsumer<?>[] cellHandlers) {

        if (parent == index) {
            final DelegateDelayedCellSetter<T, P> delayedCellSetter = new DelegateDelayedCellSetter<T, P>(this, index, breakDetector);
            cellHandlers[index] = delayedCellSetter.getCellHandler();
            return delayedCellSetter;
        } else {
            return new DelegateDelayedCellSetter<T, P>((CsvMapperCellConsumer<P>) cellHandlers[parent], index);
        }
	}

    @Override
    public String toString() {
        return "DelegateMarkerDelayedCellSetter{" +
                "mapper=" + mapper +
                ", setter=" + setter +
                '}';
    }
}
