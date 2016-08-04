package org.simpleflatmapper.core.map.column;

import org.simpleflatmapper.core.map.mapper.ColumnDefinition;
import org.simpleflatmapper.core.map.FieldKey;
import org.simpleflatmapper.core.map.FieldMapper;
import org.simpleflatmapper.core.reflect.Getter;
import org.simpleflatmapper.core.reflect.getter.GetterFactory;
import org.simpleflatmapper.core.reflect.meta.PropertyMeta;
import org.simpleflatmapper.util.Predicate;

import java.lang.reflect.Type;

public class FieldMapperColumnDefinition<K extends FieldKey<K>> extends ColumnDefinition<K, FieldMapperColumnDefinition<K>> {

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
    public Getter<?, ?> getCustomGetter() {
        final GetterProperty property = lookFor(GetterProperty.class);
        if (property != null) {
            return (Getter<?, ?>) property.getGetter();
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
    public GetterFactory<?, K> getCustomGetterFactory(){
        final GetterFactoryProperty property = lookFor(GetterFactoryProperty.class);
        if (property != null) {
            return (GetterFactory<?, K>) property.getGetterFactory();
        }
        return null;
    }

    public boolean hasCustomFactory() {
        return has(GetterFactoryProperty.class);
    }

    public FieldMapperColumnDefinition<K> addGetter(Getter<?, ?> getter) {
        return add(new GetterProperty(getter));
    }
    public FieldMapperColumnDefinition<K> addGetterFactory(GetterFactory<?, K> getterFactory) {
        return add(new GetterFactoryProperty(getterFactory));

    }
    public FieldMapperColumnDefinition<K> addFieldMapper(FieldMapper<?, ?> mapper){
        return add(new FieldMapperProperty(mapper));
    }

    @SuppressWarnings("unchecked")
    public static <K extends FieldKey<K>> FieldMapperColumnDefinition<K> identity() {
        return IDENTITY;
    }

    public static <K extends FieldKey<K>> FieldMapperColumnDefinition<K> compose(final FieldMapperColumnDefinition<K> def1, final FieldMapperColumnDefinition<K> def2) {
        return def1.compose(def2);
    }

    public static <K extends FieldKey<K>> FieldMapperColumnDefinition<K> customFieldMapperDefinition(final FieldMapper<?, ?> mapper) {
        return FieldMapperColumnDefinition.<K>identity().addFieldMapper(mapper);
    }

    public static <K extends FieldKey<K>> FieldMapperColumnDefinition<K> customGetter(final Getter<?, ?> getter) {
        return FieldMapperColumnDefinition.<K>identity().addGetter(getter);
    }

    public static <K extends FieldKey<K>> FieldMapperColumnDefinition<K> customGetterFactory(final GetterFactory<?, K> getterFactory) {
        return FieldMapperColumnDefinition.<K>identity().addGetterFactory(getterFactory);
    }

    public static <K extends FieldKey<K>> FieldMapperColumnDefinition<K> renameDefinition(final String name) {
        return FieldMapperColumnDefinition.<K>identity().addRename(name);
    }

    public static <K extends FieldKey<K>> FieldMapperColumnDefinition<K> ignoreDefinition() {
        return FieldMapperColumnDefinition.<K>identity().addIgnore();
    }

    public static <K extends FieldKey<K>> FieldMapperColumnDefinition<K> key() {
        return FieldMapperColumnDefinition.<K>identity().addKey();
    }

    public static <K extends FieldKey<K>> FieldMapperColumnDefinition<K> key(Predicate<PropertyMeta<?, ?>> predicate) {
        return FieldMapperColumnDefinition.<K>identity().addKey(predicate);
    }

    @Override
    protected FieldMapperColumnDefinition<K> newColumnDefinition(ColumnProperty[] properties) {
        return FieldMapperColumnDefinition.<K>of(properties);
    }

    @SuppressWarnings("unchecked")
    public static <K extends FieldKey<K>> FieldMapperColumnDefinition<K> of(ColumnProperty[] properties) {
        if (properties == null || properties.length == 0) {
            return IDENTITY;
        }
        return new FieldMapperColumnDefinition<K>(properties);
    }
}
