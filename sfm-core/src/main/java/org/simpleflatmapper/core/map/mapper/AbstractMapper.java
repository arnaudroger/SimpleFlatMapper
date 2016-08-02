package org.simpleflatmapper.core.map.mapper;

import org.simpleflatmapper.core.map.*;
import org.simpleflatmapper.core.reflect.Instantiator;
import org.simpleflatmapper.core.utils.ErrorHelper;

public abstract class AbstractMapper<S, T> implements Mapper<S, T> {
	
	private final Instantiator<? super S, T> instantiator;

	public AbstractMapper(final Instantiator<? super S, T> instantiator) {
		this.instantiator = instantiator;
    }

    @Override
    public final T map(final S source) throws MappingException {
        return map(source, null);
    }

    @Override
	public final T map(final S source, final MappingContext<? super S> mappingContext) throws MappingException {
		try {
			final T target = instantiator.newInstance(source);
			mapFields(source, target, mappingContext);
			return target;
		} catch(Exception e) {
            return ErrorHelper.rethrow(e);
		}
	}

    @Override
    public final void mapTo(final S source, final T target, final MappingContext<? super S> mappingContext) throws MappingException {
        try {
            mapToFields(source, target, mappingContext);
        } catch(Exception e) {
            ErrorHelper.rethrow(e);
        }
    }

    protected abstract void mapFields(final S source, final T target, final MappingContext<? super S> mappingContext) throws Exception;

    protected abstract void mapToFields(final S source, final T target, final MappingContext<? super S> mappingContext) throws Exception;

    protected void appendToStringBuilder(StringBuilder sb) {
        sb.append("instantiator=").append(String.valueOf(instantiator));
    }


}
