package org.sfm.map.impl;

import org.sfm.map.ColumnDefinition;
import org.sfm.map.FieldKey;
import org.sfm.map.FieldMapper;
import org.sfm.map.GetterFactory;
import org.sfm.map.column.*;
import org.sfm.reflect.Getter;
import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.utils.Predicate;

import java.lang.reflect.Type;

public class FieldMapperColumnDefinition<K extends FieldKey<K>, S> extends ColumnDefinition<K, FieldMapperColumnDefinition<K, S>> {

    private static final FieldMapperColumnDefinition IDENTITY = new FieldMapperColumnDefinition(new ColumnProperty[0]);

    public FieldMapperColumnDefinition(ColumnProperty[] properties) {
        super(properties);
    }

    public FieldMapper<?, ?> getCustomFieldMapper() {
        FieldMapperProperty prop = lookFor(FieldMapperProperty.class);
        if (prop != null) {
            return prop.getFieldMapper();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public Getter<S, ?> getCustomGetter() {
        final GetterProperty property = lookFor(GetterProperty.class);
        if (property != null) {
            return (Getter<S, ?>) property.getGetter();
        }
        return null;
    }

    @Override
    public boolean hasCustomSource() {
        return has(GetterProperty.class);
    }

    @Override
    public Type getCustomSourceReturnType() {
        return lookFor(GetterProperty.class).getReturnType();
    }

    @SuppressWarnings("unchecked")
    public GetterFactory<S, K> getCustomGetterFactory(){
        final GetterFactoryProperty property = lookFor(GetterFactoryProperty.class);
        if (property != null) {
            return (GetterFactory<S, K>) property.getGetterFactory();
        }
        return null;
    }

    public boolean hasCustomFactory() {
        return has(GetterFactoryProperty.class);
    }

    public FieldMapperColumnDefinition<K, S> addGetter(Getter<S, ?> getter) {
        return add(new GetterProperty(getter));
    }
    public FieldMapperColumnDefinition<K, S> addGetterFactory(GetterFactory<S, K> getterFactory) {
        return add(new GetterFactoryProperty(getterFactory));

    }
    public FieldMapperColumnDefinition<K, S> addFieldMapper(FieldMapper<S, ?> mapper){
        return add(new FieldMapperProperty(mapper));
    }

    @SuppressWarnings("unchecked")
    public static <K extends FieldKey<K>, S> FieldMapperColumnDefinition<K, S> identity() {
        return IDENTITY;
    }

    public static <K extends FieldKey<K>, S> FieldMapperColumnDefinition<K, S> compose(final FieldMapperColumnDefinition<K, S> def1, final FieldMapperColumnDefinition<K, S> def2) {
        return def1.compose(def2);
    }

    public static <K extends FieldKey<K>, S> FieldMapperColumnDefinition<K, S> customFieldMapperDefinition(final FieldMapper<S, ?> mapper) {
        return FieldMapperColumnDefinition.<K,S>identity().addFieldMapper(mapper);
    }

    public static <K extends FieldKey<K>, S> FieldMapperColumnDefinition<K, S> customGetter(final Getter<S, ?> getter) {
        return FieldMapperColumnDefinition.<K,S>identity().addGetter(getter);
    }

    public static <K extends FieldKey<K>, S> FieldMapperColumnDefinition<K, S> customGetterFactory(final GetterFactory<S, K> getterFactory) {
        return FieldMapperColumnDefinition.<K,S>identity().addGetterFactory(getterFactory);
    }

    public static <K extends FieldKey<K>, S> FieldMapperColumnDefinition<K, S> renameDefinition(final String name) {
        return FieldMapperColumnDefinition.<K,S>identity().addRename(name);
    }

    public static <K extends FieldKey<K>, S> FieldMapperColumnDefinition<K, S> ignoreDefinition() {
        return FieldMapperColumnDefinition.<K,S>identity().addIgnore();
    }

    public static <K extends FieldKey<K>, S> FieldMapperColumnDefinition<K, S> key() {
        return FieldMapperColumnDefinition.<K,S>identity().addKey();
    }

    public static <K extends FieldKey<K>, S> FieldMapperColumnDefinition<K, S> key(Predicate<PropertyMeta<?, ?>> predicate) {
        return FieldMapperColumnDefinition.<K,S>identity().addKey(predicate);
    }

    @Override
    protected FieldMapperColumnDefinition<K, S> newColumnDefinition(ColumnProperty[] properties) {
        return FieldMapperColumnDefinition.<K, S>of(properties);
    }

    public static <K extends FieldKey<K>, S> FieldMapperColumnDefinition<K, S> of(ColumnProperty[] properties) {
        if (properties == null || properties.length == 0) {
            return IDENTITY;
        }
        return new FieldMapperColumnDefinition<K, S>(properties);
    }
}
