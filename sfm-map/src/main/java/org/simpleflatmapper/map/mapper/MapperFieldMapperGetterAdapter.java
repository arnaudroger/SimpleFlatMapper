package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.fieldmapper.FieldMapperGetter;
import org.simpleflatmapper.util.Predicate;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public final class MapperFieldMapperGetterAdapter<S, P> implements FieldMapperGetter<S, P> {

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
    public P get(S s, MappingContext<?> context) {
        if (nullChecker.test(s)){
            return null;
        }
        P value = mapper.map(s, (MappingContext<? super S>) context);
        if (context != null) {
            context.setCurrentValue(valueIndex, value);
        }
        return value;
    }
}
