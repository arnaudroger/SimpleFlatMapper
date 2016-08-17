package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.converter.Converter;

import java.util.*;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class MapPropertyFinder<T extends Map<K, V>, K, V> implements PropertyFinder<T> {

    private final ClassMeta<V> valueMetaData;
    private final ClassMeta<T> mapMeta;
    private final Converter<? super CharSequence, ? extends K> keyConverter;
    private final Map<PropertyNameMatcher, PropertyFinder<V>> finders = new HashMap<PropertyNameMatcher, PropertyFinder<V>>();
    private final Map<String, MapElementPropertyMeta<?, K, V>> keys = new HashMap<String, MapElementPropertyMeta<?, K, V>>();

    public MapPropertyFinder(ClassMeta<T> mapMeta, ClassMeta<V> valueMetaData, Converter<? super CharSequence, ? extends K> keyConverter) {
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
                        return keyProperty(keyMatcher);
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
        final PropertyMeta<T, V> keyProperty = keyProperty(keyMatcher);
        final SubPropertyMeta<T, V, E> subPropertyMeta =
                new SubPropertyMeta<T, V, E>(
                        valueMetaData.getReflectionService(),
                        keyProperty,
                        (PropertyMeta<V, E>) propertyMeta);
        return subPropertyMeta;
    }

    private <E> PropertyMeta<T, E> keyProperty(PropertyNameMatcher propertyNameMatcher) throws Exception {
        String keyStringValue = propertyNameMatcher.toString();
        PropertyMeta<T, E> propertyMeta = (PropertyMeta<T, E>) keys.get(keyStringValue);

        if (propertyMeta == null) {
            MapElementPropertyMeta<T, K, V> mapElementPropertyMeta = new MapElementPropertyMeta<T, K, V>(
                    propertyNameMatcher,
                    valueMetaData.getReflectionService(),
                    valueMetaData,
                    keyConverter.convert(keyStringValue));
            keys.put(keyStringValue, mapElementPropertyMeta);
            propertyMeta =
                    (PropertyMeta<T, E>)
                            mapElementPropertyMeta;

        }
        return propertyMeta;
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
