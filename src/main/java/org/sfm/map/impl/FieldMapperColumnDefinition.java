package org.sfm.map.impl;

import org.sfm.map.ColumnDefinition;
import org.sfm.map.FieldKey;
import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Type;
import java.sql.ResultSet;


public abstract class FieldMapperColumnDefinition<K extends FieldKey<K>, S> extends ColumnDefinition<K> {

    public abstract FieldMapper<?, ?> getCustomFieldMapper();

    public abstract Getter<S, ?> getCustomGetter();

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
        return new IdentityColumnDefinition<K, S>() {
            @Override
            public FieldMapper<?, ?> getCustomFieldMapper() {
                return mapper;
            }
        };
    }

    public static <K extends FieldKey<K>, S> FieldMapperColumnDefinition<K, S> customGetter(final Getter<S, ?> getter) {
        return new IdentityColumnDefinition<K, S>() {
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
        };
    }

    public static <K extends FieldKey<K>, S> FieldMapperColumnDefinition<K, S> renameDefinition(final String name) {
        return new IdentityColumnDefinition<K, S>() {
            @Override
            public K rename(K key) {
                return key.alias(name);
            }
        };
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
        public FieldMapper<?, ?> getCustomFieldMapper() {
            return null;
        }

        @Override
        public Getter<S, ?> getCustomGetter() {
            return null;
        }
    }

    static final class ComposeColumnDefinition<K extends FieldKey<K>, S> extends FieldMapperColumnDefinition<K, S> {
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
        public boolean hasCustomSource() {
            return def1.hasCustomSource() || def2.hasCustomSource();
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
    }
}
