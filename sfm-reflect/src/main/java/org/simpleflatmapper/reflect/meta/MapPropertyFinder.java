package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.converter.Converter;

import java.util.*;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class MapPropertyFinder<T extends Map<K, V>, K, V> extends PropertyFinder<T> {

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
    protected void lookForProperties(
            PropertyNameMatcher propertyNameMatcher,
            MatchingProperties matchingProperties,
            PropertyMatchingScore score) {
        for(PropertyNameMatcherKeyValuePair keyValue : propertyNameMatcher.keyValuePairs()) {
            final PropertyNameMatcher keyMatcher = keyValue.getKey();
            final PropertyNameMatcher valueMatcher = keyValue.getValue();

            final PropertyFinder<V> propertyFinder = getPropertyFinder(keyMatcher);

            final PropertyMeta<V, ?> propertyMeta = propertyFinder.findProperty(valueMatcher);

            if (propertyMeta != null) {
                Consumer<Object> selectionCallback = new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        finders.put(keyMatcher, propertyFinder);
                    }
                };

                PropertyMeta<T, ?> keyProperty = keyProperty(keyMatcher);

                if (keyProperty != null) {
                    if (propertyMeta instanceof DirectClassMeta.DirectPropertyMeta) {
                        matchingProperties.found(keyProperty, selectionCallback, score);
                    } else {
                        matchingProperties.found(newSubPropertyMeta(keyProperty, propertyMeta), selectionCallback, score);
                    }
                }
            }
        }

    }

    private PropertyFinder<V> getPropertyFinder(PropertyNameMatcher keyMatcher) {
        PropertyFinder<V> propertyFinder = finders.get(keyMatcher);
        if (propertyFinder == null) {
            propertyFinder = valueMetaData.newPropertyFinder();
        }
        return propertyFinder;
    }

    private <E> PropertyMeta<T, E> newSubPropertyMeta(PropertyMeta<T, ?> keyProperty, PropertyMeta<V, ?> propertyMeta)  {
        return
            new SubPropertyMeta<T, V, E>(
                valueMetaData.getReflectionService(),
                (PropertyMeta<T, V>) keyProperty,
                (PropertyMeta<V, E>) propertyMeta);
    }

    private <E> PropertyMeta<T, E> keyProperty(PropertyNameMatcher propertyNameMatcher)  {
        String keyStringValue = propertyNameMatcher.toString();
        PropertyMeta<T, E> propertyMeta = (PropertyMeta<T, E>) keys.get(keyStringValue);

        if (propertyMeta == null) {

            K key;
            try {
                key = keyConverter.convert(keyStringValue);
            } catch (Exception e) {
                // invalid key..
                return null;
            }


            MapElementPropertyMeta<T, K, V> mapElementPropertyMeta = new MapElementPropertyMeta<T, K, V>(
                    propertyNameMatcher,
                    valueMetaData.getReflectionService(),
                    valueMetaData,
                    key);
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
