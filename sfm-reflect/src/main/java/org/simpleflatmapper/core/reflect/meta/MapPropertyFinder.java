package org.simpleflatmapper.core.reflect.meta;

import org.simpleflatmapper.core.reflect.InstantiatorDefinition;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.converter.Converter;

import java.util.*;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class MapPropertyFinder<T extends Map<K, V>, K, V> implements PropertyFinder<T> {

    private final ClassMeta<V> valueMetaData;
    private final ClassMeta<T> mapMeta;
    private final Converter<CharSequence, K> keyConverter;
    private final Map<PropertyNameMatcher, PropertyFinder<V>> finders = new HashMap<PropertyNameMatcher, PropertyFinder<V>>();

    public MapPropertyFinder(ClassMeta<T> mapMeta, ClassMeta<V> valueMetaData, Converter<CharSequence, K> keyConverter) {
        this.mapMeta = mapMeta;
        this.valueMetaData = valueMetaData;
        this.keyConverter = keyConverter;
    }

    @Override
    public <E> PropertyMeta<T, E> findProperty(PropertyNameMatcher propertyNameMatcher) {
        try {
            for(PropertyNameMatcherKeyValuePair keyValue : propertyNameMatcher.keyValuePairs())
            {
                final PropertyNameMatcher keyMatcher = keyValue.getKey();
                final PropertyNameMatcher valueMatcher = keyValue.getValue();

                PropertyFinder<V> propertyFinder = finders.get(keyMatcher);
                if (propertyFinder == null) {
                    propertyFinder = valueMetaData.newPropertyFinder();
                }

                final PropertyMeta<V, E> propertyMeta = propertyFinder.findProperty(valueMatcher);

                if (propertyMeta != null) {
                    finders.put(keyMatcher, propertyFinder);
                    if (propertyMeta instanceof DirectClassMeta.DirectPropertyMeta) {
                        return newKeyProperty(keyMatcher);
                    } else {
                        return newSubPropertyMeta(keyMatcher, propertyMeta);
                    }
                }
            }
        } catch(Exception e) {
            ErrorHelper.rethrow(e);
        }
        return null;
    }

    private <E> PropertyMeta<T, E> newSubPropertyMeta(PropertyNameMatcher keyMatcher, PropertyMeta<V, ?> propertyMeta) throws Exception {
        final PropertyMeta<T, V> keyProperty = newKeyProperty(keyMatcher);
        final SubPropertyMeta<T, V, E> subPropertyMeta =
                new SubPropertyMeta<T, V, E>(
                        valueMetaData.getReflectionService(),
                        keyProperty,
                        (PropertyMeta<V, E>) propertyMeta);
        return subPropertyMeta;
    }

    private <E> PropertyMeta<T, E> newKeyProperty(PropertyNameMatcher propertyNameMatcher) throws Exception {
        return (PropertyMeta<T, E>)
                new MapElementPropertyMeta<T, K, V>(
                        propertyNameMatcher,
                        valueMetaData.getReflectionService(),
                        valueMetaData,
                        keyConverter.convert(propertyNameMatcher.toString()));
    }

    @Override
    public List<InstantiatorDefinition> getEligibleInstantiatorDefinitions() {
        return mapMeta.getInstantiatorDefinitions();
    }

    @Override
    public PropertyFinder<?> getSubPropertyFinder(String name) {
        return null;
    }
}
