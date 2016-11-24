package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.util.Predicate;

import java.util.*;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class MapPropertyFinder<T extends Map<K, V>, K, V> extends PropertyFinder<T> {

    private final ClassMeta<V> valueMetaData;
    private final ClassMeta<T> mapMeta;
    private final Converter<? super CharSequence, ? extends K> keyConverter;
    private final Map<PropertyNameMatcher, PropertyFinder<V>> finders = new HashMap<PropertyNameMatcher, PropertyFinder<V>>();
    private final Map<String, MapElementPropertyMeta<?, K, V>> keys = new HashMap<String, MapElementPropertyMeta<?, K, V>>();

    public MapPropertyFinder(ClassMeta<T> mapMeta, ClassMeta<V> valueMetaData, Converter<? super CharSequence, ? extends K> keyConverter, Predicate<PropertyMeta<?, ?>> propertyFilter) {
        super(propertyFilter);
        this.mapMeta = mapMeta;
        this.valueMetaData = valueMetaData;
        this.keyConverter = keyConverter;
    }

    @Override
    protected void lookForProperties(
            final PropertyNameMatcher propertyNameMatcher,
            final FoundProperty matchingProperties,
            final PropertyMatchingScore score, boolean allowSelfReference) {
        for(final PropertyNameMatcherKeyValuePair keyValue : propertyNameMatcher.keyValuePairs()) {
            final PropertyNameMatcher keyMatcher = keyValue.getKey();
            final PropertyNameMatcher valueMatcher = keyValue.getValue();

            final PropertyFinder<V> propertyFinder = getPropertyFinder(keyMatcher);

            propertyFinder.lookForProperties(valueMatcher,
                    new FoundProperty<V>() {
                        @Override
                        public <P extends PropertyMeta<V, ?>> void found(final P propertyMeta, final Runnable selectionCallback, final PropertyMatchingScore score) {
                            Runnable sCallback = new Runnable() {
                                @Override
                                public void run() {
                                    finders.put(keyMatcher, propertyFinder);
                                    selectionCallback.run();
                                }
                            };

                            PropertyMeta<T, ?> keyProperty = keyProperty(keyMatcher);

                            if (keyProperty != null) {
                                if (propertyMeta instanceof SelfPropertyMeta) {
                                    matchingProperties.found(keyProperty, selectionCallback, score);
                                } else {
                                    matchingProperties.found(newSubPropertyMeta(keyProperty, propertyMeta), selectionCallback, score);
                                }
                            }
                        }
                    },
                    score,
                    true);
        }

    }

    private PropertyFinder<V> getPropertyFinder(PropertyNameMatcher keyMatcher) {
        PropertyFinder<V> propertyFinder = finders.get(keyMatcher);
        if (propertyFinder == null) {
            propertyFinder = valueMetaData.newPropertyFinder(propertyFilter);
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
                    mapMeta.getType(), valueMetaData.getReflectionService(),
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
