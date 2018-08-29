package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.util.ErrorHelper;

public abstract class AbstractMapper<S, T> implements SourceFieldMapper<S, T> {
	
	private final BiInstantiator<? super S, MappingContext<? super S>, T> instantiator;

	public AbstractMapper(final BiInstantiator<? super S, MappingContext<? super S>, T> instantiator) {
		this.instantiator = instantiator;
    }
    
    
    @Override
	public final T map(final S source, final MappingContext<? super S> mappingContext) throws MappingException {
		try {
			final T target = instantiator.newInstance(source, mappingContext);
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
