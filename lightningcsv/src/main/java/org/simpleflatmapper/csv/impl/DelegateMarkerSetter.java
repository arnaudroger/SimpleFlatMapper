package org.simpleflatmapper.csv.impl;

import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.mapper.CellSetter;
import org.simpleflatmapper.reflect.Setter;

public class DelegateMarkerSetter<T, P> implements CellSetter<T> {

	private final CsvMapperImpl<P> mapper;
	private final Setter<? super T, ? super P> setter;
    private final int parent;
 
	public DelegateMarkerSetter(CsvMapperImpl<P> mapper, Setter<? super T, ? super P> setter, int parent) {
		this.mapper = mapper;
		this.setter = setter;
        this.parent = parent;
	}

	@Override
	public void set(T target, char[] chars, int offset, int length, ParsingContext parsingContext)
			throws Exception {
		throw new UnsupportedOperationException();
	}

	public CsvMapperImpl<P> getMapper() {
		return mapper;
	}

	public Setter<? super T, ? super P> getSetter() {
		return setter;
	}

    public int getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "DelegateMarkerSetter{" +
                "jdbcMapper=" + mapper +
                ", setter=" + setter +
                '}';
    }
}
