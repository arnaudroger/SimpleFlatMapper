package org.simpleflatmapper.core.reflect.getter;

import  org.simpleflatmapper.core.map.Mapper;
import org.simpleflatmapper.core.reflect.Getter;
import org.simpleflatmapper.core.utils.Predicate;

import static org.simpleflatmapper.core.utils.Asserts.requireNonNull;

public final class MapperGetterAdapter<S, P> implements Getter<S, P> {

	private final Mapper<S, P> mapper;
    private final Predicate<S> nullChecker;
	
	public MapperGetterAdapter(Mapper<S, P> mapper, Predicate<S> nullChecker) {
		this.mapper = requireNonNull("jdbcMapper", mapper);
        this.nullChecker = requireNonNull("nullChecker", nullChecker);
    }

	@Override
	public P get(S target) throws Exception {
        if (nullChecker.test(target)){
            return null;
        }
		return mapper.map(target);
	}

    @Override
    public String toString() {
        return "MapperGetterAdapter{" +
                "jdbcMapper=" + mapper +
                '}';
    }
}
