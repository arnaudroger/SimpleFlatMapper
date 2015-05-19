package org.sfm.reflect.meta;

import org.sfm.reflect.InstantiatorDefinition;
import org.sfm.tuples.Tuple2;
import org.sfm.utils.ErrorHelper;
import org.sfm.utils.conv.Converter;

import java.util.*;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class MapPropertyFinder<T extends Map<K, V>, K, V> implements PropertyFinder<T> {

    private final ClassMeta<V> valueMetaData;
    private final Converter<CharSequence, K> keyConverter;
    private Map<PropertyNameMatcher, PropertyFinder<V>> finders = new HashMap<PropertyNameMatcher, PropertyFinder<V>>();

    public MapPropertyFinder(ClassMeta<V> valueMetaData, Converter<CharSequence, K> keyConverter) {
        this.valueMetaData = valueMetaData;
        this.keyConverter = keyConverter;
    }

    @Override
    public <E> PropertyMeta<T, E> findProperty(PropertyNameMatcher propertyNameMatcher) {
        try {
            for(Tuple2<PropertyNameMatcher, PropertyNameMatcher> keyValue : propertyNameMatcher.keyValuePairs())
            {
                final PropertyNameMatcher keyMatcher = keyValue.first();
                final PropertyNameMatcher valueMatcher = keyValue.second();

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

    public <E> PropertyMeta<T, E> newSubPropertyMeta(PropertyNameMatcher keyMatcher, PropertyMeta propertyMeta) throws Exception {
        final PropertyMeta<T, E> keyProperty = newKeyProperty(keyMatcher);
        final SubPropertyMeta<T, E> subPropertyMeta = new SubPropertyMeta<T, E>(valueMetaData.getReflectionService(), keyProperty, propertyMeta);
        return subPropertyMeta;
    }

    public <E> PropertyMeta<T, E> newKeyProperty(PropertyNameMatcher propertyNameMatcher) throws Exception {
        return (PropertyMeta<T, E>)
                new MapElementPropertyMeta<T, K, V>(
                        propertyNameMatcher,
                        valueMetaData.getReflectionService(),
                        valueMetaData,
                        keyConverter.convert(propertyNameMatcher.toString()));
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
