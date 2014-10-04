package org.sfm.map;

import org.sfm.reflect.Instantiator;

public class MapperImpl<S, T> extends AbstractMapperImpl<S, T> {
	
	private final FieldMapper<S, T>[] fieldMappers;
	
	public MapperImpl(final FieldMapper<S, T>[] mappers, final Instantiator<S, T> instantiator) {
		super(instantiator);
		this.fieldMappers = mappers;
	}

	protected final void mapFields(final S source, final T target) throws Exception {
		for(int i = 0; i < fieldMappers.length; i++) {
			fieldMappers[i].map(source, target);
		}
	}
}
