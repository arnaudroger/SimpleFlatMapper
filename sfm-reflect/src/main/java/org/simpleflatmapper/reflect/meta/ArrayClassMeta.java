package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

public class ArrayClassMeta<T, E> implements ClassMeta<T> {

	private final ReflectionService reflectionService;
	private final Type elementTarget;
	private final ClassMeta<E> elementClassMeta;
	private final Type type;
	private final InstantiatorDefinition constructor;

	public ArrayClassMeta(Type type, Type elementTarget, ReflectionService reflectionService) {
		this.type = type;
		this.elementTarget = elementTarget;
		this.reflectionService = reflectionService;
		this.elementClassMeta = reflectionService.getClassMeta(elementTarget);
		this.constructor = getConstructor(type);
	}

	private InstantiatorDefinition getConstructor(Type type) {

		if (TypeHelper.isArray(type)) {
			return null;
		} else {
			Class<?> implClass = findListImpl(type);
			try {
				return new ExecutableInstantiatorDefinition(implClass.getDeclaredConstructor());
			} catch (NoSuchMethodException e) {
				throw new IllegalArgumentException("No empty constructor for " + implClass);
			}
		}
	}

	private Class<?> findListImpl(Type type) {
		Class<?> clazz = TypeHelper.toClass(type);

		if (clazz.isInterface()) {
			if (List.class.equals(clazz)) {
				return ArrayList.class;
			}
		} else if (!Modifier.isAbstract(clazz.getModifiers())) {
			return clazz;
		}

		throw new IllegalArgumentException("No known List impl for " + type);
	}

	public ClassMeta<E> getElementClassMeta() {
		return elementClassMeta;
	}
	
	public Type getElementTarget() {
		return elementTarget;
	}

	@Override
	public ReflectionService getReflectionService() {
		return reflectionService;
	}

	@Override
	public PropertyFinder<T> newPropertyFinder(Predicate<PropertyMeta<?, ?>> propertyFilter) {
		return new ArrayPropertyFinder<T, E>(this, propertyFilter);
	}

	public Type getType() {
		return type;
	}

    public boolean isArray() {
        return TypeHelper.isArray(type);
    }

	@Override
	public List<InstantiatorDefinition> getInstantiatorDefinitions() {
		if (constructor != null) {
			return Arrays.asList(constructor);
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public void forEachProperties(Consumer<? super PropertyMeta<T, ?>> consumer) {
		throw new UnsupportedOperationException("Cannot forEach property on array as variable");
	}
}
