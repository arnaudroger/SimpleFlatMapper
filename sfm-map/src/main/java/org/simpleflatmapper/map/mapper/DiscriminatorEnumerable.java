package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.Function;
import org.simpleflatmapper.util.Predicate;


public class DiscriminatorEnumerable<S, T> implements Enumerable<T> {

    private final PredicatedMapperWithContext<S, T>[] discriminatorMappers;
    private final Enumerable<S> sourceEnumerable;
    private final Function<? super S, ? extends CharSequence> errorMessageGenerator;

    private T currentValue;
    private T nextValue;
    private SourceFieldMapper<S, T> currentMapper;
    private MappingContext<? super S> currentMappingContext;
    private boolean exhausted = false;


    public DiscriminatorEnumerable(
            PredicatedMapperWithContext<S, T>[] discriminatorMappers,
            Enumerable<S> sourceEnumerable,
            Function<? super S, ? extends CharSequence> errorMessageGenerator) {
        this.discriminatorMappers = discriminatorMappers;
        this.sourceEnumerable = sourceEnumerable;
        this.errorMessageGenerator = errorMessageGenerator;
    }

    @Override
    public boolean next() {
        if (exhausted) return false;
        try {
            currentValue = nextValue;
            nextValue = null;
            while (sourceEnumerable.next()) {

                checkMapper();

                S source = sourceEnumerable.currentValue();

                if (currentMappingContext.broke(source)) {
                    if (currentValue == null) {
                        currentValue = currentMapper.map(source, currentMappingContext);
                    } else {
                        nextValue = currentMapper.map(source, currentMappingContext);
                        return true;
                    }
                } else {
                    currentMapper.mapTo(source, currentValue, currentMappingContext);
                }
            }

            exhausted = true;

            return currentValue != null;
        } catch (Exception e) {
            ErrorHelper.rethrow(e);
            return false;
        }
    }

    public T currentValue() {
        return currentValue;
    }

    private void checkMapper() {
        for(PredicatedMapperWithContext<S, T> pmm : discriminatorMappers ) {
            if (pmm.predicate.test(sourceEnumerable.currentValue())) {
                if (pmm.mapper != currentMapper) {
                    markAsBroken();
                    currentMapper = pmm.mapper;
                    currentMappingContext = pmm.mappingContext;
                }
                return;
            }
        }
        mapperNotFound();
    }

    private void mapperNotFound() {
        CharSequence errorMessage = errorMessageGenerator.apply(sourceEnumerable.currentValue());
        throw new MappingException("No mapper found for " + errorMessage);
    }

    private void markAsBroken() {
        for(PredicatedMapperWithContext<S, T> pmm : discriminatorMappers ) {
           pmm.mappingContext.markAsBroken();
        }
    }

    public static class PredicatedMapperWithContext<ROW, T> {
        private final Predicate<ROW> predicate;
        private final SourceFieldMapper<ROW, T> mapper;
        private final MappingContext<? super ROW> mappingContext;

        public PredicatedMapperWithContext(Predicate<ROW> predicate, SourceFieldMapper<ROW, T> mapper, MappingContext<? super ROW> mappingContext) {
            this.predicate = predicate;
            this.mapper = mapper;
            this.mappingContext = mappingContext;
        }
    }
}
