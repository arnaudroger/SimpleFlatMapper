package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.RowHandlerErrorHandler;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.util.Enumarable;
import org.simpleflatmapper.util.UnaryFactory;

public class JoinMapperImpl<ROW, ROWS, T, EX extends Exception> extends AbstractEnumarableDelegateMapper<ROW, ROWS, T, EX> {

    private final Mapper<ROW, T> mapper;
    private final MappingContextFactory<? super ROW> mappingContextFactory;
    private final UnaryFactory<ROWS, Enumarable<ROW>> factory;

    public JoinMapperImpl(Mapper<ROW, T> mapper, RowHandlerErrorHandler errorHandler, MappingContextFactory<? super ROW> mappingContextFactory, UnaryFactory<ROWS, Enumarable<ROW>> factory) {
        super(errorHandler);
        this.mapper = mapper;
        this.mappingContextFactory = mappingContextFactory;
        this.factory = factory;
    }


    @Override
    protected final Mapper<ROW, T> getMapper(ROW source) {
        return mapper;
    }

    @Override
    protected final Enumarable<T> newEnumarableOfT(ROWS source) throws EX {
        return new JoinMapperEnumarable<ROW, T>(mapper,  mappingContextFactory.newContext(), newSourceEnumarable(source));
    }

    private Enumarable<ROW> newSourceEnumarable(ROWS source) {
        return factory.newInstance(source);
    }

    public MappingContext<? super ROW> newMappingContext() {
        return mappingContextFactory.newContext();
    }


    public MappingContext<? super ROW> newMappingContext(ROW row) {
        return newMappingContext();
    }

}
