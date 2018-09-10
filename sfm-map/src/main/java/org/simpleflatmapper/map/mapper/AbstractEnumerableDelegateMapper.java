package org.simpleflatmapper.map.mapper;


import org.simpleflatmapper.map.ContextualSourceMapper;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.ConsumerErrorHandler;

public abstract class AbstractEnumerableDelegateMapper<ROW, SET, T, E extends Exception> extends AbstractEnumerableMapper<SET, T, E> implements SetRowMapper<ROW, SET, T, E> {
    
    public AbstractEnumerableDelegateMapper(ConsumerErrorHandler errorHandler) {
        super(errorHandler);
    }
    @Override
    public final T map(ROW source) throws MappingException {
        return getMapper(source).map(source);
    }

    @Override
    public final T map(ROW source, MappingContext<? super ROW> context) throws MappingException {
        return getMapper(source).map(source, context);
    }

    protected abstract ContextualSourceMapper<ROW, T> getMapper(ROW source);
}
