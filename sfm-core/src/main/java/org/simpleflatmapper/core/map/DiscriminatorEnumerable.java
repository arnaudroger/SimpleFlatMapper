package org.simpleflatmapper.core.map;

import org.simpleflatmapper.core.tuples.Tuple3;
import org.simpleflatmapper.core.utils.Enumarable;
import org.simpleflatmapper.core.utils.ErrorHelper;
import org.simpleflatmapper.core.utils.Predicate;
import org.simpleflatmapper.core.conv.Converter;

public class DiscriminatorEnumerable<S, T> implements Enumarable<T> {

    private final Tuple3<Predicate<S>, Mapper<S, T>, MappingContext<? super S>>[] discriminatorMappers;
    private final Enumarable<S> sourceEnumarable;
    private final Converter<S, String> errorMessageGenerator;

    private T currentValue;
    private T nextValue;
    private Mapper<S, T> currentMapper;
    private MappingContext<? super S> currentMappingContext;


    public DiscriminatorEnumerable(
            Tuple3<Predicate<S>, Mapper<S, T>, MappingContext<? super S>>[] discriminatorMappers,
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
        for(Tuple3<Predicate<S>, Mapper<S, T>, MappingContext<? super S>> pmm : discriminatorMappers ) {
            if (pmm.first().test(sourceEnumarable.currentValue())) {
                if (pmm.second() != currentMapper) {
                    markAsBroken();
                    currentMapper = pmm.second();
                    currentMappingContext = pmm.third();
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
        for(Tuple3<Predicate<S>, Mapper<S, T>, MappingContext<? super S>> pmm : discriminatorMappers ) {
           pmm.third().markAsBroken();
        }
    }
}
