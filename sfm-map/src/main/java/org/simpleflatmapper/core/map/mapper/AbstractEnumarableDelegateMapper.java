package org.simpleflatmapper.core.map.mapper;


import org.simpleflatmapper.core.map.Mapper;
import org.simpleflatmapper.core.map.MappingContext;
import org.simpleflatmapper.core.map.MappingException;
import org.simpleflatmapper.core.map.RowHandlerErrorHandler;

public abstract class AbstractEnumarableDelegateMapper<R, S, T, E extends Exception> extends AbstractEnumarableMapper<S, T, E> implements Mapper<R, T> {
    public AbstractEnumarableDelegateMapper(RowHandlerErrorHandler errorHandler) {
        super(errorHandler);
    }

    @Override
    public final T map(R source) throws MappingException {
        return getMapper(source).map(source);
    }

    @Override
    public final T map(R source, MappingContext<? super R> context) throws MappingException {
        return getMapper(source).map(source, context);
    }

    @Override
    public final void mapTo(R source, T target, MappingContext<? super R> context) throws Exception {
        getMapper(source).mapTo(source, target, context);
    }

    protected abstract Mapper<R, T> getMapper(R source);
}
