package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.context.Key;
import org.simpleflatmapper.map.context.impl.BreakDetector;
import org.simpleflatmapper.util.ArrayListEnumerable;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.ErrorHelper;

import java.util.ArrayList;

public class UnorderedJoinMapperEnumerable<S, T> implements Enumerable<T> {

    private final SourceFieldMapper<S, T> mapper;
    private final MappingContext<? super S> mappingContext;


    private final Enumerable<S> sourceEnumerable;
    private final BreakDetector<? super S> breakDetector;

    private Enumerable<T> objectsEnumerable;

    public UnorderedJoinMapperEnumerable(SourceFieldMapper<S, T> mapper,
                                         MappingContext<? super S> mappingContext,
                                         Enumerable<S> sourceEnumerable,
                                         BreakDetector<? super S> breakDetector) {
        this.mapper = mapper;
        this.mappingContext = mappingContext;
        this.sourceEnumerable = sourceEnumerable;
        this.breakDetector = breakDetector;
    }

    @Override
    public boolean next() {
        try {
            if (objectsEnumerable == null) objectsEnumerable = fetchAll();
            return objectsEnumerable.next();
        } catch (Exception e) {
            ErrorHelper.rethrow(e);
            return false;
        }

    }

    private Enumerable<T> fetchAll() throws Exception {
        ArrayList<T> objects = new ArrayList<T>();
        while (sourceEnumerable.next()) {

            S source = sourceEnumerable.currentValue();

            mappingContext.handleSource(source); // set current key
            
            T currentValue = (T) breakDetector.getValue();
            
            if (currentValue == null) {
                currentValue = mapper.map(source, mappingContext);
                breakDetector.setValue(currentValue);
                objects.add(currentValue);
            } else {
                mapper.mapTo(source, currentValue, mappingContext);
            }
        }
        
        return new ArrayListEnumerable<T>(objects);
    }

    @Override
    public T currentValue() {
        return objectsEnumerable.currentValue();
    }

    @Override
    public String toString() {
        return "UnorderedJoinMapperEnumerable{" +
                "jdbcMapper=" + mapper +
                '}';
    }
}
