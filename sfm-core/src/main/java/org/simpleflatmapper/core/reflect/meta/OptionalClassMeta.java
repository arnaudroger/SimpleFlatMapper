package org.simpleflatmapper.core.reflect.meta;

import org.simpleflatmapper.core.reflect.*;
import org.simpleflatmapper.core.utils.ErrorHelper;

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
					reflectionService,
					instantiatorDefinition.getParameters()[0],
					TypeHelper.toClass(type), new ScoredGetter<Optional<T>, Object>(Integer.MAX_VALUE, new OptionalGetter<T>()), instantiatorDefinition);
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

    public InstantiatorDefinition getInstantiatorDefinition() {
        return instantiatorDefinition;
    }

    @Override
	public ReflectionService getReflectionService() {
		return reflectionService;
	}

	@Override
	public PropertyFinder<Optional<T>> newPropertyFinder() {
		return new OptionalPropertyFinder<T>(this);
	}

	public Type getType() {
		return type;
	}

	@Override
	public String[] generateHeaders() {
		return innerMeta.generateHeaders();
	}

	public ClassMeta<T> getInnerMeta() {
		return innerMeta;
	}

    public PropertyMeta<Optional<T>, ?> getProperty() {
        return propertyMeta;
    }

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public List<InstantiatorDefinition> getInstantiatorDefinitions() {
		return Arrays.asList(instantiatorDefinition);
	}


	private static class OptionalGetter<T> implements Getter<Optional<T>, Object> {
		@Override
        public Object get(Optional<T> target) throws Exception {
            return target.orElse(null);
        }
	}
}
