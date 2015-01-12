package org.sfm.map.impl;

import org.sfm.map.ColumnDefinition;

import java.sql.ResultSet;


public abstract class FieldMapperColumnDefinition<K extends FieldKey<K>> extends ColumnDefinition<K> {

    public abstract FieldMapper<?, ?> getFieldMapper();

    public static <K extends FieldKey<K>> FieldMapperColumnDefinition<K> identity() {
        return new IndentityColumnDefinition<K>();
    }

    public static <K extends FieldKey<K>> FieldMapperColumnDefinition<K> compose(final FieldMapperColumnDefinition<K> def1, final FieldMapperColumnDefinition<K> def2) {
        return new ComposeColumnDefinition<K>(def1, def2);
    }

    public static <K extends FieldKey<K>> FieldMapperColumnDefinition<K> customFieldMapperDefinition(final FieldMapper<ResultSet, ?> mapper) {
        return new IndentityColumnDefinition<K>() {
            @Override
            public FieldMapper<?, ?> getFieldMapper() {
                return mapper;
            }
        };
    }
    public static <K extends FieldKey<K>> FieldMapperColumnDefinition<K> renameDefinition(final String name) {
        return new IndentityColumnDefinition<K>() {
            @Override
            public K rename(K key) {
                return key.alias(name);
            }
        };
    }
    static class IndentityColumnDefinition<K extends FieldKey<K>> extends FieldMapperColumnDefinition<K> {
        @Override
        public K rename(K key) {
            return key;
        }


        @Override
        public FieldMapper<?, ?> getFieldMapper() {
            return null;
        }
    }

    static final class ComposeColumnDefinition<K extends FieldKey<K>> extends FieldMapperColumnDefinition<K> {
        private final FieldMapperColumnDefinition<K> def1;
        private final FieldMapperColumnDefinition<K> def2;

        public ComposeColumnDefinition(FieldMapperColumnDefinition<K> def1, FieldMapperColumnDefinition<K> def2) {
            this.def1 = def1;
            this.def2 = def2;
        }

        @Override
        public K rename(K key) {
            return def2.rename(def1.rename(key));
        }

        @Override
        public FieldMapper<?, ?> getFieldMapper() {
            FieldMapper<?, ?> fm = def1.getFieldMapper();

            if (fm == null) {
                fm = def2.getFieldMapper();
            }
            return fm;
        }
    }
}
