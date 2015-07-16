package org.sfm.map.impl;

import org.sfm.map.Mapper;
import org.sfm.map.MappingContext;
import org.sfm.map.MappingContextFactory;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.utils.Enumarable;
import org.sfm.utils.UnaryFactory;

public class JoinMapperImpl<R, S, T, E extends Exception> extends AbstractEnumarableDelegateMapper<R, S, T, E> {

    private final Mapper<R, T> mapper;
    private final MappingContextFactory<? super R> mappingContextFactory;
    private final UnaryFactory<S, Enumarable<R>> factory;

    public JoinMapperImpl(Mapper<R, T> mapper, RowHandlerErrorHandler errorHandler, MappingContextFactory<? super R> mappingContextFactory, UnaryFactory<S, Enumarable<R>> factory) {
        super(errorHandler);
        this.mapper = mapper;
        this.mappingContextFactory = mappingContextFactory;
        this.factory = factory;
    }


    @Override
    protected final Mapper<R, T> getMapper(R source) {
        return mapper;
    }

    @Override
    protected final Enumarable<T> newEnumarableOfT(S source) throws E {
        return new JoinEnumarable<R, T>(mapper,  mappingContextFactory.newContext(), newSourceEnumarable(source));
    }

    private final Enumarable<R> newSourceEnumarable(S source) {
        return factory.newInstance(source);
    }

    public MappingContext<? super R> newMappingContext() {
        return mappingContextFactory.newContext();
    }


    public MappingContext<? super R> newMappingContext(R row) {
        return newMappingContext();
    }

}
