package org.sfm.csv.impl;

import org.sfm.reflect.Setter;

public class DelegateMarkerSetter<T, P> implements CellSetter<T> {

	private final CsvMapperImpl<P> mapper;
	private final Setter<T, P> setter;
 
	public DelegateMarkerSetter(CsvMapperImpl<P> mapper, Setter<T, P> setter) {
		this.mapper = mapper;
		this.setter = setter;
	}

	@Override
	public void set(T target, char[] chars, int offset, int length, ParsingContext parsingContext)
			throws Exception {
		throw new UnsupportedOperationException();
	}

	public CsvMapperImpl<P> getMapper() {
		return mapper;
	}


	public Setter<T, P> getSetter() {
		return setter;
	}

    @Override
    public String toString() {
        return "DelegateMarkerSetter{" +
                "mapper=" + mapper +
                ", setter=" + setter +
                '}';
    }
}
