package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.util.BiFactory;
import org.simpleflatmapper.util.Predicate;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public final class MapperBiFactoryAdapter<S, P> implements BiFactory<S, MappingContext<? super S>, P> {

	private final Mapper<S, P> mapper;
    private final Predicate<S> nullChecker;
    private final int valueIndex;
	
	public MapperBiFactoryAdapter(Mapper<S, P> mapper, Predicate<S> nullChecker, int valueIndex) {
		this.mapper = requireNonNull("jdbcMapper", mapper);
        this.nullChecker = requireNonNull("nullChecker", nullChecker);
        this.valueIndex = valueIndex;
    }

    @Override
    public String toString() {
        return "MapperBiFactoryAdapter{" +
                "jdbcMapper=" + mapper +
                '}';
    }

    @Override
    public P newInstance(S s, MappingContext<? super S> mappingContext) {
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
