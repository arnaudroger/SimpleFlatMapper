package org.simpleflatmapper.map.mapper;


import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.ConsumerErrorHandler;

public abstract class AbstractEnumarableDelegateMapper<ROW, ROWS, T, E extends Exception> extends AbstractEnumarableMapper<ROWS, T, E> implements Mapper<ROW, T> {
    public AbstractEnumarableDelegateMapper(ConsumerErrorHandler errorHandler) {
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

    @Override
    public final void mapTo(ROW source, T target, MappingContext<? super ROW> context) throws Exception {
        getMapper(source).mapTo(source, target, context);
    }

    protected abstract Mapper<ROW, T> getMapper(ROW source);
}
