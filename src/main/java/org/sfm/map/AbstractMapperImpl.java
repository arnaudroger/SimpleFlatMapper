package org.sfm.map;

import org.sfm.reflect.Instantiator;

public abstract class AbstractMapperImpl<S, T> implements Mapper<S, T> {
	
	private final Instantiator<S, T> instantiator;
	
	public AbstractMapperImpl(final Instantiator<S, T> instantiator) {
		this.instantiator = instantiator;
	}

	@Override
	public final T map(final S source) throws MappingException {
		try {
			final T target = instantiator.newInstance(source);
			mapFields(source, target);
			return target;
		} catch(Exception e) {
			throw new MappingException(e.getMessage(), e);
		}
	}

	protected abstract void mapFields(final S source, final T target) throws Exception;
	
}
