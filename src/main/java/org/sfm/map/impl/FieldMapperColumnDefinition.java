package org.sfm.map.impl;

import org.sfm.map.ColumnDefinition;
import org.sfm.map.FieldKey;
import org.sfm.map.GetterFactory;
import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Type;
import java.sql.ResultSet;


public abstract class FieldMapperColumnDefinition<K extends FieldKey<K>, S> extends ColumnDefinition<K, FieldMapperColumnDefinition<K, S>> {

    public abstract FieldMapper<?, ?> getCustomFieldMapper();

    public abstract Getter<S, ?> getCustomGetter();
    public abstract GetterFactory<S, K> getCustomGetterFactory();
    public abstract boolean hasCustomFactory();
    public abstract boolean isKey();

    public abstract FieldMapperColumnDefinition<K, S> addKey();
    public abstract FieldMapperColumnDefinition<K, S> addGetter(Getter<S, ?> getter);
    public abstract FieldMapperColumnDefinition<K, S> addGetterFactory(GetterFactory<S, K> getterFactory);
    public abstract FieldMapperColumnDefinition<K, S> addFieldMapper(FieldMapper<ResultSet, ?> mapper);
    public abstract FieldMapperColumnDefinition<K, S> compose(FieldMapperColumnDefinition<K, S> columnDefinition);

    public static <K extends FieldKey<K>, S> FieldMapperColumnDefinition<K, S> identity() {
        return new IdentityColumnDefinition<K, S>();
    }

    public static <K extends FieldKey<K>, S> FieldMapperColumnDefinition<K, S> compose(final FieldMapperColumnDefinition<K, S> def1, final FieldMapperColumnDefinition<K, S> def2) {
        if (def1.getClass().equals(IdentityColumnDefinition.class)) {
            return def2;
        }
        if (def2.getClass().equals(IdentityColumnDefinition.class)) {
            return def1;
        }
        return new ComposeColumnDefinition<K, S>(def1, def2);
    }

    public static <K extends FieldKey<K>, S> FieldMapperColumnDefinition<K, S> customFieldMapperDefinition(final FieldMapper<ResultSet, ?> mapper) {
        return new CustomFieldMapperColumnDefinition<K, S>(mapper);
    }

    public static <K extends FieldKey<K>, S> FieldMapperColumnDefinition<K, S> customGetter(final Getter<S, ?> getter) {
        return new GetterColumnDefinition<K, S>(getter);
    }

    public static <K extends FieldKey<K>, S> FieldMapperColumnDefinition<K, S> customGetterFactory(final GetterFactory<S, K> getterFactory) {
        return new GetterFactoryColumnDefinition<K, S>(getterFactory);
    }

    public static <K extends FieldKey<K>, S> FieldMapperColumnDefinition<K, S> renameDefinition(final String name) {
        return new RenameColumnDefinition<K, S>(name);
    }

    public static <K extends FieldKey<K>, S> FieldMapperColumnDefinition<K, S> ignoreDefinition() {
        return new IgnoreColumnDefinition<K, S>();
    }

    public static <K extends FieldKey<K>, S> FieldMapperColumnDefinition<K, S> key() {
        return new KeyColumnDefinition<K, S>();
    }

    static class IdentityColumnDefinition<K extends FieldKey<K>, S> extends FieldMapperColumnDefinition<K, S> {
        @Override
        public K rename(K key) {
            return key;
        }

        @Override
        public boolean hasCustomSource() {
            return false;
        }

        @Override
        public Type getCustomSourceReturnType() {
            throw new IllegalStateException();
        }

        @Override
        public boolean ignore() {
            return false;
        }

        @Override
        public FieldMapper<?, ?> getCustomFieldMapper() {
            return null;
        }

        @Override
        public Getter<S, ?> getCustomGetter() {
            return null;
        }

        @Override
        public GetterFactory<S, K> getCustomGetterFactory() {
            return null;
        }

        @Override
        public boolean hasCustomFactory() {
            return false;
        }

        @Override
        public boolean isKey() {
            return false;
        }

        @Override
        public FieldMapperColumnDefinition<K, S> addKey() {
            FieldMapperColumnDefinition<K, S> columnDefinition = key();
            return compose(columnDefinition);
        }

        @Override
        public FieldMapperColumnDefinition<K, S> addRename(String name) {
            FieldMapperColumnDefinition<K, S> columnDefinition = renameDefinition(name);
            return compose(columnDefinition);
        }

        @Override
        public FieldMapperColumnDefinition<K, S> addIgnore() {
            FieldMapperColumnDefinition<K, S> columnDefinition = ignoreDefinition();
            return compose(columnDefinition);
        }

        @Override
        protected void appendToStringBuilder(StringBuilder sb) {
            sb.append("Identity{}");
        }

        @Override
        public FieldMapperColumnDefinition<K, S> addGetter(Getter<S, ?> getter) {
            FieldMapperColumnDefinition<K, S> columnDefinition = customGetter(getter);
            return compose(columnDefinition);
        }

        @Override
        public FieldMapperColumnDefinition<K, S> addGetterFactory(GetterFactory<S, K> getterFactory) {
            FieldMapperColumnDefinition<K, S> columnDefinition = customGetterFactory(getterFactory);
            return compose(columnDefinition);
        }

        @Override
        public FieldMapperColumnDefinition<K, S> addFieldMapper(FieldMapper<ResultSet, ?> mapper) {
            FieldMapperColumnDefinition<K, S> columnDefinition = customFieldMapperDefinition(mapper);
            return compose(columnDefinition);
        }

        @Override
        public FieldMapperColumnDefinition<K, S> compose(FieldMapperColumnDefinition<K, S> columnDefinition) {
            return compose(this, columnDefinition);
        }
    }

    static final class ComposeColumnDefinition<K extends FieldKey<K>, S> extends IdentityColumnDefinition<K, S> {
        private final FieldMapperColumnDefinition<K, S> def1;
        private final FieldMapperColumnDefinition<K, S> def2;

        public ComposeColumnDefinition(FieldMapperColumnDefinition<K, S> def1, FieldMapperColumnDefinition<K, S> def2) {
            this.def1 = def1;
            this.def2 = def2;
        }

        @Override
        public K rename(K key) {
            return def2.rename(def1.rename(key));
        }

        @Override
        public FieldMapper<?, ?> getCustomFieldMapper() {
            FieldMapper<?, ?> fm = def1.getCustomFieldMapper();

            if (fm == null) {
                fm = def2.getCustomFieldMapper();
            }
            return fm;
        }

        @Override
        public Getter<S, ?> getCustomGetter() {
            Getter<S, ?> fm = def1.getCustomGetter();

            if (fm == null) {
                fm = def2.getCustomGetter();
            }
            return fm;
        }
        @Override
        public GetterFactory<S, K> getCustomGetterFactory() {
            GetterFactory<S, K> fm = def1.getCustomGetterFactory();

            if (fm == null) {
                fm = def2.getCustomGetterFactory();
            }
            return fm;
        }

        @Override
        public boolean ignore() {
            return def1.ignore() || def2.ignore();
        }

        @Override
        public boolean hasCustomSource() {
            return def1.hasCustomSource() || def2.hasCustomSource();
        }

        @Override
        public boolean hasCustomFactory() {
            return def1.hasCustomFactory() || def2.hasCustomFactory();
        }

        @Override
        public Type getCustomSourceReturnType() {
            if (def1.hasCustomSource()) {
                return def1.getCustomSourceReturnType();
            } else if (def2.hasCustomSource()){
                return def2.getCustomSourceReturnType();
            } else {
                throw new IllegalStateException();
            }
        }

        @Override
        public boolean isKey() {
            return def1.isKey() || def2.isKey();
        }

        @Override
        protected void appendToStringBuilder(StringBuilder sb) {
            def1.appendToStringBuilder(sb);
            sb.append(", ");
            def2.appendToStringBuilder(sb);
        }
    }

    private static class CustomFieldMapperColumnDefinition<K extends FieldKey<K>, S> extends IdentityColumnDefinition<K, S> {
        private final FieldMapper<ResultSet, ?> mapper;

        public CustomFieldMapperColumnDefinition(FieldMapper<ResultSet, ?> mapper) {
            this.mapper = mapper;
        }

        @Override
        public FieldMapper<?, ?> getCustomFieldMapper() {
            return mapper;
        }

        @Override
        protected void appendToStringBuilder(StringBuilder sb) {
            sb.append("FieldMapper{").append(mapper).append("}");
        }
    }

    private static class GetterColumnDefinition<K extends FieldKey<K>, S> extends IdentityColumnDefinition<K, S> {
        private final Getter<S, ?> getter;

        public GetterColumnDefinition(Getter<S, ?> getter) {
            this.getter = getter;
        }

        @Override
        public Getter<S, ?> getCustomGetter() {
            return getter;
        }

        @Override
        public boolean hasCustomSource() {
            return true;
        }

        @Override
        public Type getCustomSourceReturnType() {
            Type[] paramTypesForInterface = TypeHelper.getParamTypesForInterface(getter.getClass(), Getter.class);
            return paramTypesForInterface != null ? paramTypesForInterface[1] : null;
        }

        @Override
        protected void appendToStringBuilder(StringBuilder sb) {
            sb.append("Getter{").append(getter).append("}");
        }
    }

    private static class GetterFactoryColumnDefinition<K extends FieldKey<K>, S> extends IdentityColumnDefinition<K, S> {
        private final GetterFactory<S, K> getterFactory;

        public GetterFactoryColumnDefinition(GetterFactory<S, K> getterFactory) {
            this.getterFactory = getterFactory;
        }

        @Override
        public GetterFactory<S, K> getCustomGetterFactory() {
            return getterFactory;
        }

        @Override
        public boolean hasCustomFactory() {
            return true;
        }

        @Override
        protected void appendToStringBuilder(StringBuilder sb) {
            sb.append("GetterFactory{").append(getterFactory).append("}");
        }
    }

    private static class RenameColumnDefinition<K extends FieldKey<K>, S> extends IdentityColumnDefinition<K, S> {
        private final String name;

        public RenameColumnDefinition(String name) {
            this.name = name;
        }

        @Override
        public K rename(K key) {
            return key.alias(name);
        }

        @Override
        protected void appendToStringBuilder(StringBuilder sb) {
            sb.append("Rename{'").append(name).append("'}");
        }
    }

    private static class IgnoreColumnDefinition<K extends FieldKey<K>, S> extends IdentityColumnDefinition<K, S> {

        @Override
        public boolean ignore() {
            return true;
        }

        @Override
        protected void appendToStringBuilder(StringBuilder sb) {
            sb.append("Ignore{}");
        }

    }

    private static class KeyColumnDefinition<K extends FieldKey<K>, S> extends IdentityColumnDefinition<K, S> {

        @Override
        public boolean isKey() {
            return true;
        }

        @Override
        protected void appendToStringBuilder(StringBuilder sb) {
            sb.append("Key{}");
        }

    }
}
