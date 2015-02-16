package org.sfm.reflect.meta;

import org.sfm.map.MapperBuildingException;
import org.sfm.reflect.ConstructorDefinition;
import org.sfm.reflect.ConstructorParameter;
import org.sfm.reflect.ReflectionService;
import org.sfm.utils.Predicate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TupleClassMeta<T> implements ClassMeta<T> {

	public static final String[] EMPTY_STRING_ARRAY = new String[0];
	private final ReflectionService reflectionService;
	private Type type;
	private List<ConstructorDefinition<T>> constructorDefinitions;

	public TupleClassMeta(Type type, ReflectionService reflectionService) {
		this.type = type;
		this.reflectionService = reflectionService;

		try {
			this.constructorDefinitions = reflectionService.extractConstructors(type);
		} catch(Exception e) {
			throw new MapperBuildingException(e.getMessage(), e);
		}
	}


	@Override
	public ReflectionService getReflectionService() {
		return reflectionService;
	}

	@Override
	public PropertyFinder<T> newPropertyFinder(PropertyMeta<?,?> propertyMeta, Predicate<PropertyMeta<?, ?>> isJoinProperty) {
		return new TuplePropertyFinder<T>(this, isJoinProperty);
	}

	public Type getType() {
		return type;
	}

	@Override
	public String[] generateHeaders() {
		List<String> strings = new ArrayList<String>();

		int i = 0;
		for(ConstructorParameter cp : constructorDefinitions.get(0).getParameters()) {
			String prefix = "element" + i;

			ClassMeta<?> classMeta = reflectionService.getClassMeta(cp.getResolvedType(), false);

			if (classMeta != null) {
				for(String prop : classMeta.generateHeaders()) {
					strings.add(prefix + "_" + prop);
				}
			} else {
				strings.add(prefix);
			}

			i++;
		}

		return strings.toArray(EMPTY_STRING_ARRAY);
	}


	public List<ConstructorDefinition<T>> getConstructorDefinitions() {
		return constructorDefinitions;
	}

	public int getTupleSize() {
		return constructorDefinitions.get(0).getParameters().length;
	}
}
