package org.sfm.map.impl;

import org.sfm.map.*;
import org.sfm.reflect.Instantiator;

public abstract class AbstractMapperImpl<S, T> implements Mapper<S, T> {
	
	private final Instantiator<S, T> instantiator;
    private final MappingContextFactory<S> mappingContextFactory;
	
	public AbstractMapperImpl(final Instantiator<S, T> instantiator, MappingContextFactory<S> mappingContextFactory) {
		this.instantiator = instantiator;
        this.mappingContextFactory = mappingContextFactory;
    }

    @Override
    public final T map(final S source) throws MappingException {
        return map(source, null);
    }

    @Override
	public final T map(final S source, final MappingContext<S> mappingContext) throws MappingException {
		try {
			final T target = instantiator.newInstance(source);
			mapFields(source, target, mappingContext);
			return target;
		} catch(Exception e) {
			throw new MappingException(e.getMessage(), e);
		}
	}

    @Override
    public final void mapTo(final S source, final T target, final MappingContext<S> mappingContext) throws MappingException {
        try {
            mapToFields(source, target, mappingContext);
        } catch(Exception e) {
            throw new MappingException(e.getMessage(), e);
        }
    }

    protected final MappingContext<S> newMappingContext() {
        return mappingContextFactory.newContext();
    }

    protected abstract void mapFields(final S source, final T target, final MappingContext<S> mappingContext) throws Exception;

    protected abstract void mapToFields(final S source, final T target, final MappingContext<S> mappingContext) throws Exception;

    protected void appendToStringBuilder(StringBuilder sb) {
        sb.append("instantiator=").append(String.valueOf(instantiator));
    }


}
