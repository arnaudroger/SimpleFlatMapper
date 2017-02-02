package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.util.BiFunction;
import org.simpleflatmapper.util.Predicate;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public final class MapperBiFunctionAdapter<S, P> implements BiFunction<S, MappingContext<? super S>, P> {

	private final Mapper<S, P> mapper;
    private final Predicate<S> nullChecker;
    private final int valueIndex;
	
	public MapperBiFunctionAdapter(Mapper<S, P> mapper, Predicate<S> nullChecker, int valueIndex) {
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
    public P apply(S s, MappingContext<? super S> mappingContext) {
        if (nullChecker.test(s)){
            return null;
        }
        P value = mapper.map(s, mappingContext);
        if (mappingContext != null) {
            mappingContext.setCurrentValue(valueIndex, value);
        }
        return value;
    }
}
