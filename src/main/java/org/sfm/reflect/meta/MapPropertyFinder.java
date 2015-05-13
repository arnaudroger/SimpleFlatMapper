package org.sfm.reflect.meta;

import org.sfm.reflect.InstantiatorDefinition;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Constructor;
import java.util.*;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class MapPropertyFinder<T extends Map<K, V>, K, V> implements PropertyFinder<T> {

    private final ClassMeta<V> valueMetaData;

    public MapPropertyFinder(ClassMeta<V> valueMetaData) {
        this.valueMetaData = valueMetaData;
    }

    @Override
    public <E> PropertyMeta<T, E> findProperty(PropertyNameMatcher propertyNameMatcher) {
        return (PropertyMeta<T, E>) new MapElementPropertyMeta<T, K, V>(propertyNameMatcher, valueMetaData.getReflectionService(), valueMetaData, (K) propertyNameMatcher.toString());
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
