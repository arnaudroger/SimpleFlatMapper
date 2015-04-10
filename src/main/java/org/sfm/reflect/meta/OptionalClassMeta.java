package org.sfm.reflect.meta;

import org.sfm.map.MapperBuildingException;
import org.sfm.reflect.InstantiatorDefinition;
import org.sfm.reflect.Parameter;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeHelper;
import org.sfm.tuples.Tuples;
import org.sfm.utils.ErrorHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class OptionalClassMeta<T> implements ClassMeta<T> {

    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    private final ReflectionService reflectionService;
	private final Type type;
	private final InstantiatorDefinition instantiatorDefinition;
    private final ConstructorPropertyMeta<T, ?> propertyMeta;

	public OptionalClassMeta(Type type, ReflectionService reflectionService) {
		this.type = type;
		this.reflectionService = reflectionService;

		try {
            this.instantiatorDefinition = getInstantiatorDefinition(type);
            this.propertyMeta = new ConstructorPropertyMeta<>("value", reflectionService, instantiatorDefinition.getParameters()[0], TypeHelper.toClass(type));
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
		List<String> strings = new ArrayList<String>();
		int i = 0;
		for(Parameter cp : instantiatorDefinition.getParameters()) {
			ClassMeta<?> classMeta = reflectionService.getClassMeta(cp.getGenericType(), false);

			if (classMeta != null) {
				for(String prop : classMeta.generateHeaders()) {
					strings.add(prop);
				}
			} else {
				strings.add("value");
			}

			i++;
		}
		return strings.toArray(EMPTY_STRING_ARRAY);
	}


    public Type getTargetType() {
        return  ((ParameterizedType) type).getActualTypeArguments()[0];
    }

    public PropertyMeta<T, ?> getProperty() {
        return propertyMeta;
    }
}
