package org.simpleflatmapper.core.reflect.meta;

import org.simpleflatmapper.core.reflect.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.core.reflect.InstantiatorDefinition;
import org.simpleflatmapper.core.reflect.ReflectionService;
import org.simpleflatmapper.core.reflect.TypeHelper;
import org.simpleflatmapper.core.conv.Converter;
import org.simpleflatmapper.core.conv.ConverterFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MapClassMeta<M extends Map<K, V>, K, V> implements ClassMeta<M> {

	private final ReflectionService reflectionService;
	private final Converter<CharSequence, K> keyConverter;
	private final ClassMeta<V> valueClassMeta;
	private final Type type;

	private final Constructor<?> constructor;

	public MapClassMeta(Type type, Type keyType, Type valueType, ReflectionService reflectionService) {
		this.type = type;
		this.keyConverter = ConverterFactory.getConverter(CharSequence.class, keyType);
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
	public PropertyFinder<M> newPropertyFinder() {
		return new MapPropertyFinder<M, K, V>(this, valueClassMeta, keyConverter);
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public String[] generateHeaders() {
		throw new UnsupportedOperationException("Cannot generate headers for map");
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public List<InstantiatorDefinition> getInstantiatorDefinitions() {
		return Arrays.<InstantiatorDefinition>asList(new ExecutableInstantiatorDefinition(constructor));
	}
}
