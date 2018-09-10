package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.util.Predicate;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public final class MapperFieldMapperGetterAdapter<S, P> implements ContextualGetter<S, P> {

	public final SourceMapper<S, P> mapper;
    public final Predicate<S> nullChecker;
    public final int valueIndex;
	
	public MapperFieldMapperGetterAdapter(SourceMapper<S, P> mapper, Predicate<S> nullChecker, int valueIndex) {
		this.mapper = requireNonNull("jdbcMapper", mapper);
        this.nullChecker = requireNonNull("nullChecker", nullChecker);
        this.valueIndex = valueIndex;
    }

    @Override
    public String toString() {
        return "MapperBiFunctionAdapter{" +
                "jdbcMapper=" + mapper +
                '}';
    }

    @Override
    public P get(S s, Context context) {
        if (nullChecker.test(s)){
            return null;
        }
        MappingContext<? super S> mappingContext = (MappingContext<? super S>) context;
        P value = mapper.map(s, mappingContext);
        if (mappingContext != null) {
            mappingContext.setCurrentValue(valueIndex, value);
        }
        return value;
    }
}
