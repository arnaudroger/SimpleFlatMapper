package org.sfm.map;

import org.sfm.reflect.Instantiator;

public class MapperImpl<S, T> implements Mapper<S, T> {
	
	private final FieldMapper<S, T>[] fieldMappers;
	private final Instantiator<S, T> instantiator;
	
	public MapperImpl(final FieldMapper<S, T>[] mappers, final Instantiator<S, T> instantiator) {
		this.fieldMappers = mappers;
		this.instantiator = instantiator;
	}

	@Override
	public final T map(final S source) throws MappingException {
		
		final T target;
		
		try {
			target = instantiator.newInstance(source);
		} catch(Exception e) {
			throw new InstantiationMappingException(e.getMessage(), e);
		}
		
		for(int i = 0; i < fieldMappers.length; i++) {
			fieldMappers[i].map(source, target);
		}
		return target;
	}
}
