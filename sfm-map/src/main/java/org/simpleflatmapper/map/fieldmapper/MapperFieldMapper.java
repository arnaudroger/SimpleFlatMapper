package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.util.BooleanProvider;
import org.simpleflatmapper.util.Predicate;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public final class MapperFieldMapper<S, T, P> implements FieldMapper<S, T> {

	private final Mapper<S, P> mapper;
	private final Setter<T, P> propertySetter;
    private final Getter<T, P> propertyGetter;
    private final Predicate<S> nullChecker;
    private final Getter<MappingContext<? super S>, BooleanProvider> breakDetectorProvider;

    public MapperFieldMapper(Mapper<S, P> mapper, Setter<T, P> propertySetter, Getter<T, P> propertyGetter, Predicate<S> nullChecker, Getter<MappingContext<? super S>, BooleanProvider> breakDetectorProvider) {
        this.mapper = requireNonNull("jdbcMapper", mapper);
        this.propertySetter = requireNonNull("propertySetter", propertySetter);
        this.propertyGetter = requireNonNull("propertyGetter", propertyGetter);
        this.nullChecker = requireNonNull("nullChecker", nullChecker);
        this.breakDetectorProvider = requireNonNull("breakDetectorProvider", breakDetectorProvider);
    }

    @Override
	public void mapTo(final S source, final T target, final MappingContext<? super S> context) throws Exception {
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
                "jdbcMapper=" + mapper +
                ", propertySetter=" + propertySetter +
                ", propertyGetter=" + propertyGetter +
                '}';
    }
}
