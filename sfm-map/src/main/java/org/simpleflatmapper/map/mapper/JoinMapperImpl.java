package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.RowHandlerErrorHandler;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.util.Enumarable;
import org.simpleflatmapper.util.UnaryFactory;

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
        return new JoinMapperEnumarable<R, T>(mapper,  mappingContextFactory.newContext(), newSourceEnumarable(source));
    }

    private Enumarable<R> newSourceEnumarable(S source) {
        return factory.newInstance(source);
    }

    public MappingContext<? super R> newMappingContext() {
        return mappingContextFactory.newContext();
    }


    public MappingContext<? super R> newMappingContext(R row) {
        return newMappingContext();
    }

}
