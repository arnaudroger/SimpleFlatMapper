package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.converter.ConverterService;
import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.converter.Converter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MapClassMeta<M extends Map<K, V>, K, V> implements ClassMeta<M> {

	private final ReflectionService reflectionService;
	private final Converter<? super CharSequence, ? extends K> keyConverter;
	private final ClassMeta<V> valueClassMeta;
	private final Type type;

	private final Constructor<?> constructor;

	public MapClassMeta(Type type, Type keyType, Type valueType, ReflectionService reflectionService) {
		this.type = type;
		this.keyConverter = ConverterService.getInstance().findConverter(CharSequence.class, keyType);
		if (keyConverter == null) {
			throw new IllegalArgumentException("Unsupported key type " + keyType);
		}
		this.reflectionService = reflectionService;
		this.valueClassMeta = reflectionService.getClassMeta(valueType);
		this.constructor = getConstructor(type);
	}

	private Constructor<?> getConstructor(Type type) {

		Class<?> implClass = findMapImpl(type);
		try {
			return implClass.getDeclaredConstructor();
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("No empty constructor for " + implClass);
		}
	}

	private Class<?> findMapImpl(Type type) {
		Class<?> clazz = TypeHelper.toClass(type);

		if (clazz.isInterface()) {
			if (Map.class.equals(clazz)) {
				return HashMap.class;
			} else if(ConcurrentMap.class.equals(clazz)) {
				return ConcurrentHashMap.class;
			}
		} else if (!Modifier.isAbstract(clazz.getModifiers())) {
			return clazz;
		}

		throw new IllegalArgumentException("No known Map impl for " + type);
	}

	@Override
	public ReflectionService getReflectionService() {
		return reflectionService;
	}

	@Override
	public PropertyFinder<M> newPropertyFinder(Predicate<PropertyMeta<?, ?>> propertyFilter) {
		return new MapPropertyFinder<M, K, V>(this, valueClassMeta, keyConverter, propertyFilter);
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public List<InstantiatorDefinition> getInstantiatorDefinitions() {
		return Arrays.<InstantiatorDefinition>asList(new ExecutableInstantiatorDefinition(constructor));
	}

	@Override
	public void forEachProperties(Consumer<? super PropertyMeta<M, ?>> consumer) {
		throw new UnsupportedOperationException("Cannot list properties as non static");
	}
}
