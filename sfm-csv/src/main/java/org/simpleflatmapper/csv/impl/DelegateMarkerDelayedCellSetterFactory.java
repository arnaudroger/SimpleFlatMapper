package org.simpleflatmapper.csv.impl;

import org.simpleflatmapper.csv.CsvMapper;
import org.simpleflatmapper.csv.mapper.BreakDetector;
import org.simpleflatmapper.csv.mapper.CsvMapperCellConsumer;
import org.simpleflatmapper.csv.mapper.DelayedCellSetter;
import org.simpleflatmapper.csv.mapper.DelayedCellSetterFactory;
import org.simpleflatmapper.reflect.Setter;

public class DelegateMarkerDelayedCellSetterFactory<T, P> implements DelayedCellSetterFactory<T, P> {

	private final CsvMapper<P> mapper;
	private final Setter<? super T, ? super P> setter;
    private final int index;
	private final int parent;

	public DelegateMarkerDelayedCellSetterFactory(CsvMapper<P> mapper, Setter<? super T, ? super P> setter, int index, int parent) {
		this.mapper = mapper;
		this.setter = setter;
        this.index = index;
        this.parent = parent;
    }

	public CsvMapper<P> getMapper() {
		return mapper;
	}

	public Setter<? super T, ? super P> getSetter() {
		return setter;
	}

	@SuppressWarnings("unchecked")
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
    public boolean hasSetter() {
        return setter != null;
    }

    @Override
    public String toString() {
        return "DelegateMarkerDelayedCellSetter{" +
                "jdbcMapper=" + mapper +
                ", setter=" + setter +
                '}';
    }
}
