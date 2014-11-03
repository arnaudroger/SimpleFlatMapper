package org.sfm.jdbc.impl.getter;

import org.sfm.map.Mapper;
import org.sfm.reflect.Getter;

public final class MapperGetterAdapter<S, P> implements Getter<S, P> {

	private final Mapper<S, P> mapper;
	
	public MapperGetterAdapter(Mapper<S, P> mapper) {
		this.mapper = mapper;
	}

	@Override
	public P get(S target) throws Exception {
		return mapper.map(target);
	}

}
