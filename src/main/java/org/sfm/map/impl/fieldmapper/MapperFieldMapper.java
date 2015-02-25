package org.sfm.map.impl.fieldmapper;

import org.sfm.map.Mapper;
import org.sfm.map.FieldMapper;
import org.sfm.map.MappingContext;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Setter;
import org.sfm.utils.BooleanProvider;
import org.sfm.utils.Predicate;

public final class MapperFieldMapper<S, T, P> implements FieldMapper<S, T> {

	private final Mapper<S, P> mapper;
	private final Setter<T, P> propertySetter;
    private final Getter<T, P> propertyGetter;
    private final Predicate<S> nullChecker;
    private final Getter<MappingContext<S>, BooleanProvider> breakDetectorProvider;

    public MapperFieldMapper(Mapper<S, P> mapper, Setter<T, P> propertySetter, Getter<T, P> propertyGetter, Predicate<S> nullChecker, Getter<MappingContext<S>, BooleanProvider> breakDetectorProvider) {
        if (mapper == null) {
            throw new NullPointerException("mapper is null");
        }
        if (propertySetter == null) {
            throw new NullPointerException("propertySetter is null");
        }
        if (propertyGetter == null) {
            throw new NullPointerException("propertyGetter is null");
        }
        if (nullChecker == null) {
            throw new NullPointerException("nullCheck is null");
        }
        if (breakDetectorProvider == null) {
            throw new NullPointerException("breakDetectorProvider is null");
        }
        this.mapper = mapper;
        this.propertySetter = propertySetter;
        this.propertyGetter = propertyGetter;
        this.nullChecker = nullChecker;
        this.breakDetectorProvider = breakDetectorProvider;
    }

    @Override
	public void mapTo(final S source, final T target, final MappingContext<S> context) throws Exception {
        if (nullChecker.test(source)){
            return;
        }

        boolean isBroken = breakDetectorProvider.get(context).getBoolean();

        if (isBroken) {
            propertySetter.set(target, mapper.map(source, context));
        } else {
            P value = propertyGetter.get(target);
            if (value != null) {
                mapper.mapTo(source, value, context);
            }
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
