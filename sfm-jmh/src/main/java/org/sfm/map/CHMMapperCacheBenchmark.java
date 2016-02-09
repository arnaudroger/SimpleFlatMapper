package org.sfm.map;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.map.mapper.MapperCache;
import org.sfm.map.mapper.MapperKey;

import java.util.Collections;
import java.util.List;


@State(Scope.Benchmark)
public class CHMMapperCacheBenchmark {


    @Param("10")
    private int maxColumns;

    @Param("10")
    private int size;
    private CHMMapperCache<JdbcColumnKey, Object> mapperCache;

    private List<MapperKey<JdbcColumnKey>> keys;
    private int index;

    @Setup
    public void setUp() {
        mapperCache = new CHMMapperCache<>();
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
        CHMMapperCacheBenchmark b = new CHMMapperCacheBenchmark();
        b.size = 10;
        b.maxColumns = 10;
        b.setUp();
        System.out.println("b.testGet() = " + b.testGet());
    }
}
