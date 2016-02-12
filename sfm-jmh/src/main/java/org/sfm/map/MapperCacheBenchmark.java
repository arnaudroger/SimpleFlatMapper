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
import java.util.List;

/*
Benchmark                               (cacheType)  (maxColumns)  (size)   Mode  Samples         Score  Score error  Units
o.s.m.MapperCacheBenchmark.testGet            ARRAY            10       1  thrpt       20  16121406.447   158015.666  ops/s
o.s.m.MapperCacheBenchmark.testGet            ARRAY            10      10  thrpt       20  14108073.832   113139.384  ops/s
o.s.m.MapperCacheBenchmark.testGet            ARRAY            10      50  thrpt       20   8762851.037    86680.010  ops/s
o.s.m.MapperCacheBenchmark.testGet            ARRAY            10     100  thrpt       20   5751702.025    43055.179  ops/s

o.s.m.MapperCacheBenchmark.testGet          T2ARRAY            10       1  thrpt       20  17073001.388   169983.684  ops/s
o.s.m.MapperCacheBenchmark.testGet          T2ARRAY            10      10  thrpt       20  14382125.371   115890.960  ops/s
o.s.m.MapperCacheBenchmark.testGet          T2ARRAY            10      50  thrpt       20   8813210.457   156576.515  ops/s
o.s.m.MapperCacheBenchmark.testGet          T2ARRAY            10     100  thrpt       20   5962971.427    57457.711  ops/s
o.s.m.MapperCacheBenchmark.testGet    TS2ARRAY_NULL            10       1  thrpt       20  16746524.746   134053.391  ops/s
o.s.m.MapperCacheBenchmark.testGet    TS2ARRAY_NULL            10      10  thrpt       20  14017128.946   140285.644  ops/s
o.s.m.MapperCacheBenchmark.testGet    TS2ARRAY_NULL            10      50  thrpt       20   8158035.310    93152.106  ops/s
o.s.m.MapperCacheBenchmark.testGet    TS2ARRAY_NULL            10     100  thrpt       20   5452888.593    52505.517  ops/s
o.s.m.MapperCacheBenchmark.testGet         TS2ARRAY            10       1  thrpt       20  17693461.656   278347.705  ops/s
o.s.m.MapperCacheBenchmark.testGet         TS2ARRAY            10      10  thrpt       20  14785642.259   190662.234  ops/s
o.s.m.MapperCacheBenchmark.testGet         TS2ARRAY            10      50  thrpt       20   9636776.010    66417.146  ops/s
o.s.m.MapperCacheBenchmark.testGet         TS2ARRAY            10     100  thrpt       20   9105677.738   128316.475  ops/s


o.s.m.MapperCacheBenchmark.testGet           SARRAY            10       1  thrpt       20  12289045.001   132318.191  ops/s
o.s.m.MapperCacheBenchmark.testGet           SARRAY            10      10  thrpt       20  10095838.825    68625.772  ops/s
o.s.m.MapperCacheBenchmark.testGet           SARRAY            10      50  thrpt       20   7221792.470   118438.909  ops/s
o.s.m.MapperCacheBenchmark.testGet           SARRAY            10     100  thrpt       20   6331906.161    69270.189  ops/s
o.s.m.MapperCacheBenchmark.testGet              CHM            10       1  thrpt       20  12680359.882   137062.367  ops/s
o.s.m.MapperCacheBenchmark.testGet              CHM            10      10  thrpt       20  11706468.766   196935.631  ops/s
o.s.m.MapperCacheBenchmark.testGet              CHM            10      50  thrpt       20   9233521.266   114946.144  ops/s
o.s.m.MapperCacheBenchmark.testGet              CHM            10     100  thrpt       20   9013868.195   113623.800  ops/s
o.s.m.MapperCacheBenchmark.testGet          S2ARRAY            10       1  thrpt       20  13911769.526   183843.161  ops/s
o.s.m.MapperCacheBenchmark.testGet          S2ARRAY            10      10  thrpt       20  13436023.116   103402.057  ops/s
o.s.m.MapperCacheBenchmark.testGet          S2ARRAY            10      50  thrpt       20   9791463.327   244663.253  ops/s
o.s.m.MapperCacheBenchmark.testGet          S2ARRAY            10     100  thrpt       20   8942192.463   142215.296  ops/s


 */
@State(Scope.Thread)
public class MapperCacheBenchmark {

    private int index;

    @Setup
    public void setUp() {
        index = 0;
    }

    @TearDown(Level.Invocation)
    public void afterRun(ParamCache paramCache) {
        index ++;
        if (index >= paramCache.keys.size()) index = 0;
    }

    @Benchmark
    public Object testGet(ParamCache paramCache) {
        return paramCache.mapperCache.get(paramCache.keys.get(index));
    }

}
