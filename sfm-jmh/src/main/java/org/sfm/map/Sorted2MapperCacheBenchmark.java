package org.sfm.map;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.map.mapper.MapperKey;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 *
 Benchmark                                   (maxColumns)  (size)   Mode  Samples         Score  Score error  Units
 o.s.m.SortedMapperCacheBenchmark.testGet              10       1  thrpt       10  14690508.922   289632.159  ops/s
 o.s.m.SortedMapperCacheBenchmark.testGet              10      20  thrpt       10  12274891.556   310490.389  ops/s
 o.s.m.SortedMapperCacheBenchmark.testGet              10     100  thrpt       10   9142067.912   228333.872  ops/s
 o.s.m.SortedMapperCacheBenchmark.testGet              10     200  thrpt       10   7740387.956   185828.030  ops/s
 Benchmark                                   (maxColumns)  (size)   Mode  Samples         Score  Score error  Units
 o.s.m.SortedMapperCacheBenchmark.testGet              10       1  thrpt       10  12505829.928   276604.780  ops/s
 o.s.m.SortedMapperCacheBenchmark.testGet              10      20  thrpt       10  13174094.088   811926.285  ops/s
 o.s.m.SortedMapperCacheBenchmark.testGet              10     100  thrpt       10  11071497.823   358908.412  ops/s
 o.s.m.SortedMapperCacheBenchmark.testGet              10     200  thrpt       10  10591487.940   243953.191  ops/s

 */

@State(Scope.Benchmark)
public class Sorted2MapperCacheBenchmark {


    public static final MapperKeyComparator MAPPER_KEY_COMPARATOR = new MapperKeyComparator();
    @Param("10")
    private int maxColumns;

    @Param("10")
    private int size;
    private SortedMapperCache2<JdbcColumnKey, Object> mapperCache;

    private List<MapperKey<JdbcColumnKey>> keys;
    private int index;

    @Setup
    public void setUp() {
        mapperCache = new SortedMapperCache2<>(MAPPER_KEY_COMPARATOR);
        keys = Utils.generateKeys(size, maxColumns);
        for(MapperKey<JdbcColumnKey> key : keys) {
            mapperCache.add(Utils.duplicateKey(key), new Object());
        }
        Collections.shuffle(keys);
        System.out.println("keys = " + keys);
    }

    @TearDown(Level.Invocation)
    public void afterRun() {
        index ++;
        if (index >= keys.size()) index = 0;
    }

    @Benchmark
    public Object testGet() {
        return mapperCache.get(keys.get(index));
    }

    public static void main(String[] args) {
        Sorted2MapperCacheBenchmark b = new Sorted2MapperCacheBenchmark();
        b.size = 10;
        b.maxColumns = 10;
        b.setUp();
        System.out.println("b.mapperCache.toString() = " + b.mapperCache.toString());
        System.out.println("b.testGet() = " + b.testGet());
    }

    private static class MapperKeyComparator implements Comparator<MapperKey<JdbcColumnKey>> {
        @Override
        public int compare(MapperKey<JdbcColumnKey> m1, MapperKey<JdbcColumnKey> m2) {
            JdbcColumnKey[] keys1 = m1.getColumns();
            JdbcColumnKey[] keys2 = m2.getColumns();
            return compareKeys(keys1, keys2);
        }

        private int compareKeys(JdbcColumnKey[] keys1, JdbcColumnKey[] keys2) {
            int d = keys1.length - keys2.length;
            if (d == 0){
                return compareEachKeys(keys1, keys2);
            }
            return d;
        }

        private int compareEachKeys(JdbcColumnKey[] keys1, JdbcColumnKey[] keys2) {
            int d;
            for(int i = 0; i < keys1.length; i++) {
                d = compareKey(keys1[i], keys2[i]);
                if (d!= 0) {
                    return d;
                }
            }
            return 0;
        }

        private int compareKey(JdbcColumnKey key1, JdbcColumnKey key2) {
            int d = key1.getName().compareTo(key2.getName());
            if (d != 0) {
                return d;
            }
            return key1.getSqlType() - key2.getSqlType();
        }
    }
}
