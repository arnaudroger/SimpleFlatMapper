package org.sfm.csv.impl;

import org.sfm.csv.CsvMapper;
import org.sfm.reflect.Setter;

import java.util.Map;

public class DelegateMarkerDelayedCellSetterFactory<T, P> implements DelayedCellSetterFactory<T, P> {

	private final CsvMapper<P> mapper;
	private final Setter<T, P> setter;
    private final int index;
	
	public DelegateMarkerDelayedCellSetterFactory(CsvMapper<P> mapper, Setter<T, P> setter, int index) {
		this.mapper = mapper;
		this.setter = setter;
        this.index = index;
    }

	public DelegateMarkerDelayedCellSetterFactory(CsvMapper<P> mapper, int index) {
		this.mapper = mapper;
        this.index = index;
        this.setter = null;
	}

	public CsvMapper<P> getMapper() {
		return mapper;
	}

	public Setter<T, P> getSetter() {
		return setter;
	}

	@Override
	public DelayedCellSetter<T, P> newCellSetter(BreakDetector breakDetector, Map<CsvMapper<?>, CsvMapperCellConsumer<?>> cellHandlers) {
        CsvMapperCellConsumer<P> cellConsumer = (CsvMapperCellConsumer<P>) cellHandlers.get(mapper);
        if (cellConsumer == null) {
            final DelegateDelayedCellSetter<T, P> delayedCellSetter = new DelegateDelayedCellSetter<T, P>(this, index, breakDetector);
            cellHandlers.put(mapper, delayedCellSetter.getCellHandler());
            return delayedCellSetter;
        } else {
            return new DelegateDelayedCellSetter<T, P>(cellConsumer, index);
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
