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
	private final Setter<? super T, ? super P> propertySetter;
    private final Predicate<? super S> nullChecker;

    private final Getter<MappingContext<? super S>, BooleanProvider> breakDetectorProvider;
    private final int currentValueIndex;

    public MapperFieldMapper(Mapper<S, P> mapper, Setter<? super T, ? super P> propertySetter, Predicate<? super S> nullChecker, Getter<MappingContext<? super S>, BooleanProvider> breakDetectorProvider, int currentValueIndex) {
        this.mapper = requireNonNull("jdbcMapper", mapper);
        this.propertySetter = requireNonNull("propertySetter", propertySetter);
        this.nullChecker = requireNonNull("nullChecker", nullChecker);
        this.breakDetectorProvider = requireNonNull("breakDetectorProvider", breakDetectorProvider);
        this.currentValueIndex = currentValueIndex;
    }

    @Override
	public void mapTo(final S source, final T target, final MappingContext<? super S> context) throws Exception {
        if (nullChecker.test(source)){
            return;
        }

        boolean isBroken = breakDetectorProvider.get(context).getBoolean();

        if (isBroken) {
            P value = mapper.map(source, context);
            propertySetter.set(target, value);
            if (context != null) {
                context.setCurrentValue(currentValueIndex, value);
            }
        } else {
            P value = (P) context.getCurrentValue(currentValueIndex);
            if (value == null) {
                throw new NullPointerException();
            }
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
