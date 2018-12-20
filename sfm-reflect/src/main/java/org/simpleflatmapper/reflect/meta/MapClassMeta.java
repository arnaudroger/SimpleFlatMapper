package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.converter.ContextFactory;
import org.simpleflatmapper.converter.ConverterService;
import org.simpleflatmapper.converter.DefaultContextFactoryBuilder;
import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.converter.ContextualConverter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MapClassMeta<M extends Map<K, V>, K, V> implements ClassMeta<M> {

	private final ReflectionService reflectionService;
	private final ContextualConverter<? super CharSequence, ? extends K> keyConverter;
	private final ContextFactory keyContextFactory;
	private final ClassMeta<V> valueClassMeta;
	private final Type type;

	private final Constructor<?> constructor;

	public MapClassMeta(Type type, Type keyType, Type valueType, ReflectionService reflectionService) {
		this.type = type;

		DefaultContextFactoryBuilder contextFactoryBuilder = new DefaultContextFactoryBuilder();
		
		this.keyConverter = ConverterService.getInstance().findConverter(CharSequence.class, keyType, contextFactoryBuilder);
		this.keyContextFactory = contextFactoryBuilder.build();
		this.reflectionService = reflectionService;
		this.valueClassMeta = reflectionService.getClassMeta(valueType);
		this.constructor = getConstructor(type);
	}

	public MapClassMeta(ReflectionService reflectionService, ContextualConverter<? super CharSequence, ? extends K> keyConverter, ContextFactory keyContextFactory, ClassMeta<V> valueClassMeta, Type type, Constructor<?> constructor) {
		this.reflectionService = reflectionService;
		this.keyConverter = keyConverter;
		this.keyContextFactory = keyContextFactory;
		this.valueClassMeta = valueClassMeta;
		this.type = type;
		this.constructor = constructor;
	}

	@Override
	public ClassMeta<M> withReflectionService(ReflectionService reflectionService) {
		return new MapClassMeta<M, K, V>(reflectionService, keyConverter, keyContextFactory, reflectionService.<V>getClassMeta(valueClassMeta.getType()), type, constructor);
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
		return new MapPropertyFinder<M, K, V>(this, valueClassMeta, keyConverter, keyContextFactory, reflectionService.selfScoreFullName());
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

	@Override
	public int getNumberOfProperties() {
		return 10000;
	}

	@Override
	public boolean needTransformer() {
		return false;
	}


}
