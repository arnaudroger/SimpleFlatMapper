package org.sfm.map.impl.fieldmapper;

import org.sfm.map.Mapper;
import org.sfm.map.FieldMapper;
import org.sfm.map.MappingContext;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Setter;
import org.sfm.utils.Predicate;

public final class MapperFieldMapper<S, T, P> implements FieldMapper<S, T> {

	private final Mapper<S, P> mapper;
	private final Setter<T, P> propertySetter;
    private final Getter<T, P> propertyGetter;
    private final Predicate<S> nullChecker;

    public MapperFieldMapper(Mapper<S, P> mapper, Setter<T, P> propertySetter, Getter<T, P> propertyGetter, Predicate<S> nullChecker) {
        this.mapper = mapper;
        this.propertySetter = propertySetter;
        this.propertyGetter = propertyGetter;
        this.nullChecker = nullChecker;
    }

    @Override
	public void mapTo(final S source, final T target, final MappingContext context) throws Exception {
        if (nullChecker.test(source)){
            return;
        }

        P prop = null;
        if (propertyGetter != null) {
            prop = propertyGetter.get(target);
        }

        if (prop == null) {
            if (propertySetter != null) {
                prop = mapper.map(source, context);
                propertySetter.set(target, prop);
            }
        } else {
            mapper.mapTo(source, prop, context);
        }
	}

    @Override
    public String toString() {
        return "MapperFieldMapper{" +
                "mapper=" + mapper +
                ", propertySetter=" + propertySetter +
                ", propertyGetter=" + propertyGetter +
                '}';
    }
}
