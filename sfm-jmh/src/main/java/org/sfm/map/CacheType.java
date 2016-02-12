package org.sfm.map;

import org.sfm.jdbc.JdbcColumnKey;
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
            return new Sorted2ArraysMapperCache<>(COMPARATOR);
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
            return new TS2ArraysMapperCache<>(null);
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


    private static class MapperKeyComparator implements Comparator<MapperKey<JdbcColumnKey>> {
        @Override
        public int compare(MapperKey<JdbcColumnKey> m1, MapperKey<JdbcColumnKey> m2) {
            JdbcColumnKey[] keys1 = m1.getColumns();
            JdbcColumnKey[] keys2 = m2.getColumns();
            return compareKeys(keys1, keys2);
        }

        private int compareKeys(JdbcColumnKey[] keys1, JdbcColumnKey[] keys2) {
            int d = keys1.length - keys2.length;
            if (d != 0){
                return d;
            }
            for(int i = 0; i < keys1.length; i++) {
                d = compareKey(keys1[i], keys2[i]);
                if (d!= 0) {
                    return d;
                }
            }
            return d;
        }

        private int compareKey(JdbcColumnKey key1, JdbcColumnKey key2) {
            int d = key1.getName().compareTo(key2.getName());
            if (d != 0) {
                return d;
            }
            return key1.getSqlType() - key2.getSqlType();
        }
    }
    private static final Comparator<MapperKey<JdbcColumnKey>> COMPARATOR = new MapperKeyComparator();

}
