package org.sfm.reflect.meta;

import org.sfm.reflect.InstantiatorDefinition;
import org.sfm.reflect.Parameter;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeHelper;
import org.sfm.utils.ErrorHelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class OptionalClassMeta<T> implements ClassMeta<T> {

    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    private final ReflectionService reflectionService;
	private final Type type;
	private final InstantiatorDefinition instantiatorDefinition;
    private final ConstructorPropertyMeta<T, ?> propertyMeta;
	private final ClassMeta<T> innerMeta;


	public OptionalClassMeta(Type type, ReflectionService reflectionService) {
		this.type = type;
		this.reflectionService = reflectionService;

		try {
            this.instantiatorDefinition = getInstantiatorDefinition(type);
            this.propertyMeta = new ConstructorPropertyMeta<>("value", reflectionService, instantiatorDefinition.getParameters()[0], TypeHelper.toClass(type));
			this.innerMeta = reflectionService.getClassMeta(instantiatorDefinition.getParameters()[0].getGenericType());
		} catch(Exception e) {
            ErrorHelper.rethrow(e);
            throw new IllegalStateException();
		}
	}

    private InstantiatorDefinition getInstantiatorDefinition(Type type) throws NoSuchMethodException {
        ParameterizedType pt = (ParameterizedType) type;
        InstantiatorDefinition id = new InstantiatorDefinition(Optional.class.getMethod("ofNullable", Object.class),
                new Parameter("value", Object.class, pt.getActualTypeArguments()[0]));
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
	public PropertyFinder<T> newPropertyFinder() {
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

    public PropertyMeta<T, ?> getProperty() {
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


}
