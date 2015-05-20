package org.sfm.reflect.meta;

import org.sfm.reflect.InstantiatorDefinition;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeHelper;
import org.sfm.utils.conv.Converter;
import org.sfm.utils.conv.ConverterFactory;

import java.lang.reflect.Type;
import java.util.*;

public class MapClassMeta<M extends Map<K, V>, K, V> implements ClassMeta<M> {

	private final ReflectionService reflectionService;
	private final Type valueType;
	private final Converter<CharSequence, K> keyConverter;
	private final ClassMeta<V> valueClassMeta;
	private final Type type;

	public MapClassMeta(Type type, Type keyType, Type valueType, ReflectionService reflectionService) {
		this.type = type;
		this.valueType = valueType;
		this.keyConverter = ConverterFactory.getConverter(CharSequence.class, keyType);
		if (keyConverter == null) {
			throw new IllegalArgumentException("Unsupported key type " + keyType);
		}
		this.reflectionService = reflectionService;
		this.valueClassMeta = reflectionService.getClassMeta(valueType);
	}

	public ClassMeta<V> getValueClassMeta() {
		return valueClassMeta;
	}
	
	public Type getValueType() {
		return valueType;
	}

	@Override
	public ReflectionService getReflectionService() {
		return reflectionService;
	}

	@Override
	public PropertyFinder<M> newPropertyFinder() {
		return new MapPropertyFinder<M, K, V>(this, valueClassMeta, keyConverter);
	}

	public Type getType() {
		return type;
	}

	@Override
	public String[] generateHeaders() {
		throw new UnsupportedOperationException("Cannot generate headers for map");
	}

    public boolean isArray() {
        return false;
    }

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public List<InstantiatorDefinition> getInstantiatorDefinitions() {
		try {
			return Arrays.asList(new InstantiatorDefinition(HashMap.class.getConstructor()));
		} catch (NoSuchMethodException e) {
			throw new Error("Unexpected error " + e, e);
		}
	}
}
