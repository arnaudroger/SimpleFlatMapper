package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.ConsumerErrorHandler;
import org.simpleflatmapper.map.ContextualSourceFieldMapper;
import org.simpleflatmapper.map.ContextualSourceMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.map.context.impl.BreakDetector;
import org.simpleflatmapper.map.context.impl.BreakDetectorMappingContext;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.UnaryFactory;

public class UnorderedJoinMapper<ROW, ROWS, T, EX extends Exception> extends AbstractEnumerableDelegateMapper<ROW, ROWS, T, EX> {

    private final ContextualSourceFieldMapper<ROW, T> mapper;
    private final MappingContextFactory<? super ROW> mappingContextFactory;
    private final UnaryFactory<ROWS, Enumerable<ROW>> factory;

    public UnorderedJoinMapper(ContextualSourceFieldMapper<ROW, T> mapper, ConsumerErrorHandler errorHandler, MappingContextFactory<? super ROW> mappingContextFactory, UnaryFactory<ROWS, Enumerable<ROW>> factory) {
        super(errorHandler);
        this.mapper = mapper;
        this.mappingContextFactory = mappingContextFactory;
        this.factory = factory;
    }


    @Override
    protected final ContextualSourceMapper<ROW, T> getMapper(ROW source) {
        return mapper;
    }

    @Override
    public final Enumerable<T> enumerate(ROWS source) throws EX {
        return new UnorderedJoinMapperEnumerable<ROW, T>(mapper, mappingContextFactory.newContext(), enumerateRows(source));
    }

    private Enumerable<ROW> enumerateRows(ROWS source) {
        return factory.newInstance(source);
    }

    protected MappingContextFactory<? super ROW> getMappingContextFactory() {
        return mappingContextFactory;
    }
}
