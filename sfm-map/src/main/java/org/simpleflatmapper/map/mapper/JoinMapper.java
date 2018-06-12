package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.ConsumerErrorHandler;
import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.util.Enumarable;
import org.simpleflatmapper.util.UnaryFactory;

public class JoinMapper<ROW, ROWS, T, EX extends Exception> extends AbstractEnumarableDelegateMapper<ROW, ROWS, T, EX> {

    private final SourceFieldMapper<ROW, T> mapper;
    private final MappingContextFactory<? super ROW> mappingContextFactory;
    private final UnaryFactory<ROWS, Enumarable<ROW>> factory;

    public JoinMapper(SourceFieldMapper<ROW, T> mapper, ConsumerErrorHandler errorHandler, MappingContextFactory<? super ROW> mappingContextFactory, UnaryFactory<ROWS, Enumarable<ROW>> factory) {
        super(errorHandler);
        this.mapper = mapper;
        this.mappingContextFactory = mappingContextFactory;
        this.factory = factory;
    }


    @Override
    protected final SourceMapper<ROW, T> getMapper(ROW source) {
        return mapper;
    }

    @Override
    protected final Enumarable<T> newEnumarableOfT(ROWS source) throws EX {
        return new JoinMapperEnumarable<ROW, T>(mapper,  mappingContextFactory.newContext(), newSourceEnumarable(source));
    }

    private Enumarable<ROW> newSourceEnumarable(ROWS source) {
        return factory.newInstance(source);
    }

    protected MappingContextFactory<? super ROW> getMappingContextFactory() {
        return mappingContextFactory;
    }
}
