package org.simpleflatmapper.map;

import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.map.mapper.MapperKeyComparator;
import org.simpleflatmapper.jdbc.impl.JdbcColumnKeyMapperKeyComparator;

public enum CacheType {

    ARRAY {
        @Override
        IMapperCache<JdbcColumnKey, Object> newCache() {
            return new ArrayMapperCache<>();
        }
    },
    SARRAY {
        @Override
        IMapperCache<JdbcColumnKey, Object> newCache() {
            return new SortedMapperCache<>(COMPARATOR);
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
    CHM {
        @Override
        IMapperCache<JdbcColumnKey, Object> newCache() {
            return new CHMMapperCache<>();
        }
    },

    ;


    abstract IMapperCache<JdbcColumnKey, Object> newCache();


    private static final MapperKeyComparator<JdbcColumnKey> COMPARATOR = JdbcColumnKeyMapperKeyComparator.INSTANCE;

}
