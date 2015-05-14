package org.sfm.reflect.meta;

import org.sfm.reflect.InstantiatorDefinition;
import org.sfm.reflect.TypeHelper;
import org.sfm.utils.ErrorHelper;
import org.sfm.utils.conv.Converter;

import java.lang.reflect.Constructor;
import java.util.*;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class MapPropertyFinder<T extends Map<K, V>, K, V> implements PropertyFinder<T> {

    private final ClassMeta<V> valueMetaData;
    private final Converter<CharSequence, K> keyConverter;

    public MapPropertyFinder(ClassMeta<V> valueMetaData, Converter<CharSequence, K> keyConverter) {
        this.valueMetaData = valueMetaData;
        this.keyConverter = keyConverter;
    }

    @Override
    public <E> PropertyMeta<T, E> findProperty(PropertyNameMatcher propertyNameMatcher) {
        try {
            final Converter<CharSequence, K> keyConverter = this.keyConverter;
            return (PropertyMeta<T, E>) new MapElementPropertyMeta<T, K, V>(propertyNameMatcher, valueMetaData.getReflectionService(), valueMetaData, keyConverter.convert(propertyNameMatcher.toString()));
        } catch(Exception e) {
            ErrorHelper.rethrow(e);
            return null;
        }
    }

    @Override
    public List<InstantiatorDefinition> getEligibleInstantiatorDefinitions() {

        try {
            return Arrays.asList(new InstantiatorDefinition(HashMap.class.getConstructor()));
        } catch (NoSuchMethodException e) {
            throw new Error("Unexpected error " + e, e);
        }

    }

    @Override
    public <E> ConstructorPropertyMeta<T, E> findConstructor(InstantiatorDefinition instantiatorDefinition) {
        return null;
    }
}
