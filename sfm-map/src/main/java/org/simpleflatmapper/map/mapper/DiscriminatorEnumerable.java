package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.converter.UncheckedConverter;
import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.util.Enumarable;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.Predicate;

public class DiscriminatorEnumerable<S, T> implements Enumarable<T> {

    private final PredicatedMapperWithContext<S, T>[] discriminatorMappers;
    private final Enumarable<S> sourceEnumarable;
    private final UncheckedConverter<? super S, ? extends CharSequence> errorMessageGenerator;

    private T currentValue;
    private T nextValue;
    private Mapper<S, T> currentMapper;
    private MappingContext<? super S> currentMappingContext;


    public DiscriminatorEnumerable(
            PredicatedMapperWithContext<S, T>[] discriminatorMappers,
            Enumarable<S> sourceEnumarable,
            UncheckedConverter<? super S, ? extends CharSequence> errorMessageGenerator) {
        this.discriminatorMappers = discriminatorMappers;
        this.sourceEnumarable = sourceEnumarable;
        this.errorMessageGenerator = errorMessageGenerator;
    }

    @Override
    public boolean next() {
        try {
            currentValue = nextValue;
            nextValue = null;
            while (sourceEnumarable.next()) {

                checkMapper();

                S source = sourceEnumarable.currentValue();

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
            if (pmm.predicate.test(sourceEnumarable.currentValue())) {
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
        CharSequence errorMessage = errorMessageGenerator.convert(sourceEnumarable.currentValue());
        throw new MappingException("No mapper found for " + errorMessage);
    }

    private void markAsBroken() {
        for(PredicatedMapperWithContext<S, T> pmm : discriminatorMappers ) {
           pmm.mappingContext.markAsBroken();
        }
    }

    public static class PredicatedMapperWithContext<ROW, T> {
        private final Predicate<ROW> predicate;
        private final Mapper<ROW, T> mapper;
        private final MappingContext<? super ROW> mappingContext;

        public PredicatedMapperWithContext(Predicate<ROW> predicate, Mapper<ROW, T> mapper, MappingContext<? super ROW> mappingContext) {
            this.predicate = predicate;
            this.mapper = mapper;
            this.mappingContext = mappingContext;
        }
    }
}
