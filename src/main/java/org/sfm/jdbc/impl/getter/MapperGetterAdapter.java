package org.sfm.jdbc.impl.getter;

import org.sfm.map.Mapper;
import org.sfm.reflect.Getter;
import org.sfm.utils.Predicate;

public final class MapperGetterAdapter<S, P> implements Getter<S, P> {

	private final Mapper<S, P> mapper;
    private final Predicate<S> nullChecker;
	
	public MapperGetterAdapter(Mapper<S, P> mapper, Predicate<S> nullChecker) {
        if (mapper == null) {
            throw new NullPointerException("mapper is null");
        }
        if (nullChecker == null) {
            throw new NullPointerException("nullChecker is null");
        }
		this.mapper = mapper;
        this.nullChecker = nullChecker;
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
                "mapper=" + mapper +
                '}';
    }
}
