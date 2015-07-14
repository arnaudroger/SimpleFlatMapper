package org.sfm.map.impl;

import org.sfm.map.Mapper;
import org.sfm.map.MappingContext;
import org.sfm.map.MappingContextFactory;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.utils.Enumarable;
import org.sfm.utils.OneArgumentFactory;

import java.sql.ResultSet;

public class JoinMapperImpl<R, S, T, E extends Exception> extends AbstractEnumarableDelegateMapper<R, S, T, E> {

    private final Mapper<R, T> mapper;
    private final MappingContextFactory<? super R> mappingContextFactory;
    private final OneArgumentFactory<S, Enumarable<R>> factory;

    public JoinMapperImpl(Mapper<R, T> mapper, RowHandlerErrorHandler errorHandler, MappingContextFactory<? super R> mappingContextFactory, OneArgumentFactory<S, Enumarable<R>> factory) {
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
}
