package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.ScoredSetter;
import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.ScoredGetter;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class OptionalClassMeta<T> implements ClassMeta<Optional<T>> {

    private final ReflectionService reflectionService;
	private final Type type;
	private final InstantiatorDefinition instantiatorDefinition;
    private final ConstructorPropertyMeta<Optional<T>, ?> propertyMeta;
	private final ClassMeta<T> innerMeta;


	public OptionalClassMeta(Type type, ReflectionService reflectionService) {
		this.type = type;
		this.reflectionService = reflectionService;

		try {
            this.instantiatorDefinition = getInstantiatorDefinition(type);
			this.propertyMeta = new ConstructorPropertyMeta<Optional<T>, Object>("value",
                    type, reflectionService,
					instantiatorDefinition.getParameters()[0],
					new ScoredGetter<Optional<T>, Object>(Integer.MAX_VALUE, new OptionalGetter<T>()),
					ScoredSetter.<Optional<T>, Object>nullSetter(),
					instantiatorDefinition);
			this.innerMeta = reflectionService.getClassMeta(instantiatorDefinition.getParameters()[0].getGenericType());
		} catch(Exception e) {
            ErrorHelper.rethrow(e);
            throw new IllegalStateException();
		}
	}

    private InstantiatorDefinition getInstantiatorDefinition(Type type) throws NoSuchMethodException {
        ParameterizedType pt = (ParameterizedType) type;
        InstantiatorDefinition id = new ExecutableInstantiatorDefinition(Optional.class.getMethod("ofNullable", Object.class),
                new Parameter(0, "value", Object.class, pt.getActualTypeArguments()[0]));
        return id;
    }


    @Override
	public ReflectionService getReflectionService() {
		return reflectionService;
	}

	@Override
	public PropertyFinder<Optional<T>> newPropertyFinder(Predicate<PropertyMeta<?, ?>> propertyFilter) {
		return new OptionalPropertyFinder<T>(this, propertyFilter);
	}

	public Type getType() {
		return type;
	}

	public ClassMeta<T> getInnerMeta() {
		return innerMeta;
	}

    public PropertyMeta<Optional<T>, ?> getProperty() {
        return propertyMeta;
    }

	@Override
	public List<InstantiatorDefinition> getInstantiatorDefinitions() {
		return Arrays.asList(instantiatorDefinition);
	}

	@Override
	public void forEachProperties(Consumer<? super PropertyMeta<Optional<T>, ?>> consumer) {
		consumer.accept(propertyMeta);
	}


	private static class OptionalGetter<T> implements Getter<Optional<T>, Object> {
		@Override
        public Object get(Optional<T> target) throws Exception {
            return target.orElse(null);
        }
	}
}
