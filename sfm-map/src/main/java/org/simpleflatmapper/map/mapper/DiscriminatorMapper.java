package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.ContextualSourceFieldMapper;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.ConsumerErrorHandler;
import org.simpleflatmapper.map.context.MappingContextFactoryFromRows;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.Function;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.UnaryFactory;

import java.util.List;

public class DiscriminatorMapper<ROW, ROWS, T, EX extends Exception> extends AbstractEnumerableDelegateMapper<ROW, ROWS, T, EX> {

    private final List<PredicatedMapper<ROW, ROWS, T, EX>> mappers;
    private final Function<ROW, String> errorConverter;
    private final UnaryFactory<ROWS, Enumerable<ROW>> rowEnumerableFactory;

    public DiscriminatorMapper(List<PredicatedMapper<ROW, ROWS, T, EX>> mappers,
                               UnaryFactory<ROWS, Enumerable<ROW>> rowEnumerableFactory,
                               Function<ROW, String> errorConverter,
                               ConsumerErrorHandler consumerErrorHandler) {
        super(consumerErrorHandler);
        this.mappers = mappers;
        this.errorConverter = errorConverter;
        this.rowEnumerableFactory = rowEnumerableFactory;
    }

    @Override
    protected ContextualSourceFieldMapper<ROW, T> getMapper(final ROW row) throws MappingException {

        for (PredicatedMapper<ROW, ROWS, T, EX> tm : mappers) {
            if (tm.getPredicate().test(row)) {
                return tm.getMapper();
            }
        }
        throw new MappingException("No mapper found for " + errorConverter.apply(row));
    }


    @SuppressWarnings("unchecked")
    @Override
    public DiscriminatorEnumerable<ROW, T> enumerate(ROWS rows) throws EX {
        DiscriminatorEnumerable.PredicatedMapperWithContext<ROW, T>[] mapperDiscriminators =
                new DiscriminatorEnumerable.PredicatedMapperWithContext[this.mappers.size()];

        for(int i = 0; i < mapperDiscriminators.length; i++) {

            PredicatedMapper<ROW, ROWS, T, EX> predicatedMapper = mappers.get(i);
            mapperDiscriminators[i] =
                    new DiscriminatorEnumerable.PredicatedMapperWithContext<ROW, T>(
                            predicatedMapper.getPredicate(),
                            predicatedMapper.getMapper(),
                            predicatedMapper.getMappingContextFactory().newMappingContext(rows));
        }

        return new DiscriminatorEnumerable<ROW, T>(
                mapperDiscriminators,
                rowEnumerableFactory.newInstance(rows),
                errorConverter);
    }

    public static class PredicatedMapper<ROW, ROWS, T, EX extends Throwable> {

        private final Predicate<ROW> predicate;
        private final ContextualSourceFieldMapper<ROW, T> mapper;
        private final MappingContextFactoryFromRows<ROW, ROWS, EX> mappingContextFactory;

        public PredicatedMapper(Predicate<ROW> predicate,
                                ContextualSourceFieldMapper<ROW, T> mapper,
                                MappingContextFactoryFromRows<ROW, ROWS, EX> mappingContextFactory) {
            this.predicate = predicate;
            this.mapper = mapper;
            this.mappingContextFactory = mappingContextFactory;
        }

        public Predicate<ROW> getPredicate() {
            return predicate;
        }

        public ContextualSourceFieldMapper<ROW, T> getMapper() {
            return mapper;
        }

        public MappingContextFactoryFromRows<ROW, ROWS, EX> getMappingContextFactory() {
            return mappingContextFactory;
        }
    }


}
