package org.sfm.map.impl.fieldmapper;

import org.sfm.map.Mapper;
import org.sfm.map.impl.FieldMapper;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Setter;

public final class MapperFieldMapper<S, T, P> implements FieldMapper<S, T> {

	private final Mapper<S, P> mapper;
	private final Setter<T, P> propertySetter;
    private final Getter<T, P> propertyGetter;

    public MapperFieldMapper(Mapper<S, P> mapper, Setter<T, P> propertySetter, Getter<T, P> propertyGetter) {
        this.mapper = mapper;
        this.propertySetter = propertySetter;
        this.propertyGetter = propertyGetter;
    }

    @Override
	public void map(final S source, final T target) throws Exception {

        P prop = null;
        if (propertyGetter != null) {
            prop = propertyGetter.get(target);
        }

        if (prop == null) {
            if (propertySetter != null) {
                prop = mapper.map(source);
                propertySetter.set(target, prop);
            }
        } else {
            mapper.mapTo(source, prop);
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
