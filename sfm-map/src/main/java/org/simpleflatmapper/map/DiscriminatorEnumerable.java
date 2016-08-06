package org.simpleflatmapper.map;

import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.util.Enumarable;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.Predicate;

public class DiscriminatorEnumerable<S, T> implements Enumarable<T> {

    private final PredicatedMapper<S, T>[] discriminatorMappers;
    private final Enumarable<S> sourceEnumarable;
    private final Converter<S, String> errorMessageGenerator;

    private T currentValue;
    private T nextValue;
    private Mapper<S, T> currentMapper;
    private MappingContext<? super S> currentMappingContext;


    public DiscriminatorEnumerable(
            PredicatedMapper<S, T>[] discriminatorMappers,
            Enumarable<S> sourceEnumarable,
            Converter<S, String> errorMessageGenerator) {
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
                currentMappingContext.handle(source);

                if (currentMappingContext.rootBroke()) {
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
        for(PredicatedMapper<S, T> pmm : discriminatorMappers ) {
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
        String errorMessage;
        try {
            errorMessage = errorMessageGenerator.convert(sourceEnumarable.currentValue());
        } catch (Exception e) {
            errorMessage = "NA";
        }
        throw new MappingException("No mapper found for " + errorMessage);
    }

    private void markAsBroken() {
        for(PredicatedMapper<S, T> pmm : discriminatorMappers ) {
           pmm.mappingContext.markAsBroken();
        }
    }

    //Tuple3<Predicate<S>, Mapper<S, T>, MappingContext<? super S>>
    public static class PredicatedMapper<S, T> {
        private final Predicate<S> predicate;
        private final Mapper<S, T> mapper;
        private final MappingContext<? super S> mappingContext;

        public PredicatedMapper(Predicate<S> predicate, Mapper<S, T> mapper, MappingContext<? super S> mappingContext) {
            this.predicate = predicate;
            this.mapper = mapper;
            this.mappingContext = mappingContext;
        }
    }
}
