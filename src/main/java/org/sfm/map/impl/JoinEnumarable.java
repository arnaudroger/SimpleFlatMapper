package org.sfm.map.impl;

import org.sfm.map.Mapper;
import org.sfm.map.MappingContext;
import org.sfm.utils.Enumarable;
import org.sfm.utils.ErrorHelper;

public class JoinEnumarable<S, T> implements Enumarable<T> {

    private final Mapper<S, T> mapper;
    private final MappingContext<? super S> mappingContext;


    private final Enumarable<S> sourceEnumarable;
    private T currentValue;
    private T nextValue;

    public JoinEnumarable(Mapper<S, T> mapper,
                          MappingContext<? super S> mappingContext,
                          Enumarable<S> sourceEnumarable) {
        this.mapper = mapper;
        this.mappingContext = mappingContext;
        this.sourceEnumarable = sourceEnumarable;
    }

    @Override
    public boolean next() {
        try {
            currentValue = nextValue;
            nextValue = null;
            while (sourceEnumarable.next()) {

                S source = sourceEnumarable.currentValue();
                mappingContext.handle(source);

                if (mappingContext.rootBroke()) {
                    if (currentValue == null) {
                        currentValue = mapper.map(source, mappingContext);
                    } else {
                        nextValue = mapper.map(source, mappingContext);
                        return true;
                    }
                } else {
                    mapper.mapTo(source, currentValue, mappingContext);
                }
            }

            return currentValue != null;
        } catch (Exception e) {
            ErrorHelper.rethrow(e);
            return false;
        }

    }

    @Override
    public T currentValue() {
        return currentValue;
    }

    @Override
    public String toString() {
        return "JoinJdbcMapper{" +
                "jdbcMapper=" + mapper +
                '}';
    }
}
