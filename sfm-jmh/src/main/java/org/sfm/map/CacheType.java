package org.sfm.map;

import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.jdbc.impl.JdbcColumnKeyComparator;
import org.sfm.map.impl.AbstractMapperKeyComparator;
import org.sfm.map.impl.MapperKeyComparator;
import org.sfm.map.mapper.MapperKey;

import java.util.Comparator;

public enum CacheType {

    ARRAY {
        @Override
        IMapperCache<JdbcColumnKey, Object> newCache() {
            return new ArrayMapperCache<>();
        }
    },
    CHM {
        @Override
        IMapperCache<JdbcColumnKey, Object> newCache() {
            return new CHMMapperCache<>();
        }
    },
    S2ARRAY {
        @Override
        IMapperCache<JdbcColumnKey, Object> newCache() {
            return new S2ArraysMapperCache<>(COMPARATOR);
        }
    },
    T2ARRAY {
        @Override
        IMapperCache<JdbcColumnKey, Object> newCache() {
            return new T2ArraysMapperCache<>();
        }
    },
    TS2ARRAY {
        @Override
        IMapperCache<JdbcColumnKey, Object> newCache() {
            return new TS2ArraysMapperCache<>(COMPARATOR);
        }
    },
    TS2ARRAY_NULL {
        @Override
        IMapperCache<JdbcColumnKey, Object> newCache() {
            return new TS2NArraysMapperCache<>(null);
        }
    },
    SARRAY {
        @Override
        IMapperCache<JdbcColumnKey, Object> newCache() {
            return new SortedMapperCache<>(COMPARATOR);
        }
    }

    ;


    abstract IMapperCache<JdbcColumnKey, Object> newCache();


    private static class MapperKeyComparatorImpl extends AbstractMapperKeyComparator<JdbcColumnKey> {

        @Override
        protected int compareKey(JdbcColumnKey key1, JdbcColumnKey key2) {
            int d = key1.getName().compareTo(key2.getName());
            if (d != 0) {
                return d;
            }
            return key1.getSqlType() - key2.getSqlType();
        }
    }
    private static final Comparator<MapperKey<JdbcColumnKey>> COMPARATOR = new MapperKeyComparator(new JdbcColumnKeyComparator());

}
