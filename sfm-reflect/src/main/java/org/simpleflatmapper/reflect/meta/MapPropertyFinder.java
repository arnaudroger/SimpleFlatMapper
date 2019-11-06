package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.converter.ContextFactory;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.converter.ContextualConverter;
import org.simpleflatmapper.reflect.property.MapTypeProperty;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class MapPropertyFinder<T extends Map<K, V>, K, V> extends PropertyFinder<T> {
    
    public static int NONE = 0;
    public static int COLUMN_AS_KEY = 1;
    public static int KEY_VALUE = 2;

    private final ClassMeta<MapKeyValueElementPropertyMeta.KeyValue<K, V>> keyValueClassMeta;
    private final ClassMeta<V> valueMetaData;
    private final ClassMeta<T> mapMeta;
    private final ContextualConverter<? super CharSequence, ? extends K> keyConverter;
    private final ContextFactory keyContextFactory;
    private final Map<PropertyNameMatcher, PropertyFinder<V>> finders = new HashMap<PropertyNameMatcher, PropertyFinder<V>>();
    private final Map<PropertyMeta<?, ?>, PropertyFinder<?>> findersByKey = new HashMap<PropertyMeta<?, ?>, PropertyFinder<?>>();
    private final Map<String, MapElementPropertyMeta<?, K, V>> keys = new HashMap<String, MapElementPropertyMeta<?, K, V>>();
    private final PropertyFinder<MapKeyValueElementPropertyMeta.KeyValue<K, V>> keyValuePropertyFinder;
    private final Type keyValueType;
    private final MapKeyValueElementPropertyMeta<T, K, V> elementPropertyMeta;

    private int keyValueMode = NONE;

    @Deprecated
    public MapPropertyFinder(ClassMeta<T> mapMeta, ClassMeta<V> valueMetaData, ContextualConverter<? super CharSequence, ? extends K> keyConverter, ContextFactory keyContextFactory, boolean selfScoreFullName) {
        this(mapMeta, valueMetaData, keyConverter, keyContextFactory);
    }
    public MapPropertyFinder(ClassMeta<T> mapMeta, ClassMeta<V> valueMetaData, ContextualConverter<? super CharSequence, ? extends K> keyConverter, ContextFactory keyContextFactory) {
        super();
        this.mapMeta = mapMeta;
        this.valueMetaData = valueMetaData;
        this.keyConverter = keyConverter;
        this.keyValueType = getKeyValueType(mapMeta);
        this.keyValueClassMeta = mapMeta.getReflectionService().getClassMeta(keyValueType);
        this.keyContextFactory = keyContextFactory;
        this.keyValuePropertyFinder = keyValueClassMeta.newPropertyFinder();
        this.elementPropertyMeta = 
            new MapKeyValueElementPropertyMeta<T, K, V>(mapMeta.getType(), valueMetaData.getReflectionService(), keyValueType);
    }

    private Type getKeyValueType(ClassMeta<T> mapMeta) {
        final Type mapType = mapMeta.getType();
        if (mapType instanceof ParameterizedType) {
            return new ParameterizedType() {
                @Override
                public Type[] getActualTypeArguments() {
                    return ((ParameterizedType) mapType).getActualTypeArguments();
                }

                @Override
                public Type getRawType() {
                    return MapKeyValueElementPropertyMeta.KeyValue.class;
                }

                @Override
                public Type getOwnerType() {
                    return null;
                }
            };
        } else {
            return MapKeyValueElementPropertyMeta.KeyValue.class;
        }
    }

    @Override
    public void lookForProperties(
            final PropertyNameMatcher propertyNameMatcher,
            Object[] properties, final FoundProperty<T> matchingProperties,
            final PropertyMatchingScore score, boolean allowSelfReference, PropertyFinderTransformer propertyFinderTransformer, TypeAffinityScorer typeAffinityScorer, PropertyFilter propertyFilter, ShortCircuiter shortCircuiter) {

        if (isKeyValueEnabled(properties)) {
            propertyFinderTransformer.apply(keyValuePropertyFinder).lookForProperties(propertyNameMatcher,
                    properties, new FoundProperty() {
                        @Override
                        public void found(final PropertyMeta propertyMeta, final Runnable selectionCallback, final PropertyMatchingScore score, TypeAffinityScorer typeAffinityScorer) {

                            Runnable sCallback = new Runnable() {
                                @Override
                                public void run() {
                                    selectionCallback.run();
                                    keyValueMode = KEY_VALUE;
                                    findersByKey.put(elementPropertyMeta, keyValuePropertyFinder);
                                }
                            };

                            matchingProperties.found(new SubPropertyMeta(propertyMeta.getReflectService(), elementPropertyMeta, propertyMeta), sCallback, score.matches(elementPropertyMeta, propertyNameMatcher, new PropertyNameMatch("key", "key", null, propertyNameMatcher.asScore(), 0 )), typeAffinityScorer);
                        }
                    },
                    score,
                    false, propertyFinderTransformer, typeAffinityScorer, propertyFilter, shortCircuiter);
        }
        if (isColunnKeyEnabled(properties)) {
            // classic keys set
            for (final PropertyNameMatcherKeyValuePair keyValue : propertyNameMatcher.keyValuePairs()) {
                final PropertyNameMatcher keyMatcher = keyValue.getKey();
                final PropertyNameMatcher valueMatcher = keyValue.getValue();

                final PropertyFinder<V> propertyFinder = getPropertyFinder(keyMatcher);

                propertyFinderTransformer.apply(propertyFinder).lookForProperties(valueMatcher,
                        properties, new FoundProperty<V>() {
                            @Override
                            public <P extends PropertyMeta<V, ?>> void found(final P propertyMeta, final Runnable selectionCallback, final PropertyMatchingScore score, TypeAffinityScorer typeAffinityScorer) {
                                final PropertyMeta<T, ?> keyProperty = keyProperty(keyMatcher);
                                Runnable sCallback = new Runnable() {
                                    @Override
                                    public void run() {
                                        finders.put(keyMatcher, propertyFinder);
                                        findersByKey.put(keyProperty, propertyFinder);
                                        selectionCallback.run();
                                        keyValueMode = COLUMN_AS_KEY;

                                    }
                                };

                                if (keyProperty != null) {
                                    matchingProperties.found(newSubPropertyMeta(keyProperty, propertyMeta), sCallback, score.matches(propertyMeta, propertyNameMatcher, new PropertyNameMatch("key", "key", null, propertyNameMatcher.asScore(), 0 )), typeAffinityScorer);
                                }
                            }
                        },
                        score,
                        true, propertyFinderTransformer, typeAffinityScorer, propertyFilter, shortCircuiter);
            }
        }

    }

    private boolean isColunnKeyEnabled(Object[] properties) {
        return keyConverter != null || keyValueMode == COLUMN_AS_KEY || (keyValueMode != KEY_VALUE && MapTypeProperty.isColumnKeyEnabled(properties)); 
    }

    private boolean isKeyValueEnabled(Object[] properties) {
        return keyConverter == null || keyValueMode == KEY_VALUE || (keyValueMode != COLUMN_AS_KEY && MapTypeProperty.isKeyValueEnabled(properties));
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
                key = keyConverter.convert(keyStringValue, keyContextFactory.newContext());
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
    public PropertyFinder<?> getSubPropertyFinder(PropertyMeta<?, ?> owner) {
        return findersByKey.get(owner);
    }

    @Override
    public PropertyFinder<?> getOrCreateSubPropertyFinder(SubPropertyMeta<?, ?, ?> subPropertyMeta) {
        return getSubPropertyFinder(subPropertyMeta.getOwnerProperty());
    }


    @Override
    public Type getOwnerType() {
        return mapMeta.getType();
    }
}
