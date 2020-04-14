package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.ErrorHelper;

public class JoinMapperEnumerable<S, T> implements Enumerable<T> {

    private final SourceFieldMapper<S, T> mapper;
    private final MappingContext<? super S> mappingContext;


    private final Enumerable<S> sourceEnumerable;
    private T currentValue;
    private T nextValue;

    private boolean exhausted = false;

    public JoinMapperEnumerable(SourceFieldMapper<S, T> mapper,
                                MappingContext<? super S> mappingContext,
                                Enumerable<S> sourceEnumerable) {
        this.mapper = mapper;
        this.mappingContext = mappingContext;
        this.sourceEnumerable = sourceEnumerable;
    }

    @Override
    public boolean next() {
        if (exhausted) return false;
        try {
            currentValue = nextValue;
            nextValue = null;
            while (sourceEnumerable.next()) {

                S source = sourceEnumerable.currentValue();

                if (mappingContext.broke(source)) {
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

            exhausted = true;

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
