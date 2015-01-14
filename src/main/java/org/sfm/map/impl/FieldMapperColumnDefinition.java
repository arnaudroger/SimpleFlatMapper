package org.sfm.map.impl;

import org.sfm.map.ColumnDefinition;
import org.sfm.reflect.Getter;

import java.sql.ResultSet;


public abstract class FieldMapperColumnDefinition<K extends FieldKey<K>, S> extends ColumnDefinition<K> {

    public abstract FieldMapper<?, ?> getCustomFieldMapper();

    public abstract Getter<S, ?> getCustomGetter();

    public static <K extends FieldKey<K>, S> FieldMapperColumnDefinition<K, S> identity() {
        return new IndentityColumnDefinition<K, S>();
    }

    public static <K extends FieldKey<K>, S> FieldMapperColumnDefinition<K, S> compose(final FieldMapperColumnDefinition<K, S> def1, final FieldMapperColumnDefinition<K, S> def2) {
        return new ComposeColumnDefinition<K, S>(def1, def2);
    }

    public static <K extends FieldKey<K>, S> FieldMapperColumnDefinition<K, S> customFieldMapperDefinition(final FieldMapper<ResultSet, ?> mapper) {
        return new IndentityColumnDefinition<K, S>() {
            @Override
            public FieldMapper<?, ?> getCustomFieldMapper() {
                return mapper;
            }
        };
    }

    public static <K extends FieldKey<K>, S> FieldMapperColumnDefinition<K, S> customGetter(final Getter<S, ?> getter) {
        return new IndentityColumnDefinition<K, S>() {
            @Override
            public Getter<S, ?> getCustomGetter() {
                return getter;
            }
        };
    }

    public static <K extends FieldKey<K>, S> FieldMapperColumnDefinition<K, S> renameDefinition(final String name) {
        return new IndentityColumnDefinition<K, S>() {
            @Override
            public K rename(K key) {
                return key.alias(name);
            }
        };
    }
    static class IndentityColumnDefinition<K extends FieldKey<K>, S> extends FieldMapperColumnDefinition<K, S> {
        @Override
        public K rename(K key) {
            return key;
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
    }
}
