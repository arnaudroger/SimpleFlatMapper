package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.util.Predicate;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public final class MapperFieldMapper<S, T, P> implements FieldMapper<S, T> {

	private final Mapper<S, P> mapper;
	private final Setter<? super T, ? super P> propertySetter;
    private final Predicate<? super S> nullChecker;

    private final int currentValueIndex;

    public MapperFieldMapper(Mapper<S, P> mapper, Setter<? super T, ? super P> propertySetter, Predicate<? super S> nullChecker, int currentValueIndex) {
        this.mapper = requireNonNull("jdbcMapper", mapper);
        this.propertySetter = requireNonNull("propertySetter", propertySetter);
        this.nullChecker = requireNonNull("nullChecker", nullChecker);
        this.currentValueIndex = currentValueIndex;
    }

    @SuppressWarnings("unchecked")
    @Override
	public void mapTo(final S source, final T target, final MappingContext<? super S> context) throws Exception {
        if (nullChecker.test(source)){
            return;
        }

        P value = null;

        if (context != null) {
            value = (P) context.getCurrentValue(currentValueIndex);
        }

        if (value == null) {
            value = mapper.map(source, context);
            if (context != null) {
                context.setCurrentValue(currentValueIndex, value);
            }
            propertySetter.set(target, value);
        } else {
            mapper.mapTo(source, value, context);
        }
	}

    @Override
    public String toString() {
        return "MapperFieldMapper{" +
                "jdbcMapper=" + mapper +
                ", propertySetter=" + propertySetter +
                '}';
    }
}
