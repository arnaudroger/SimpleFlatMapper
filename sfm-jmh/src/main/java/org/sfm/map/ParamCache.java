package org.sfm.map;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.map.mapper.MapperKey;

import java.util.List;

@State(Scope.Benchmark)
public class ParamCache {

    @Param(value = {"ARRAY", "SARRAY", "T2ARRAY", "TS2ARRAY_NULL", "TS2ARRAY", "CHM", "S2ARRAY" })
    private CacheType cacheType;

    IMapperCache<JdbcColumnKey, Object> mapperCache;

    List<MapperKey<JdbcColumnKey>> keys;

    @Param("10")
    private int maxColumns;
    @Param({"1", "10", "50","100"})
    private int size;

    @Setup
    public void setUp() {
        mapperCache = cacheType.newCache();
        keys = Utils.generateKeys(size, maxColumns);
        for (MapperKey<JdbcColumnKey> key : keys) {
            mapperCache.add(Utils.duplicateKey(key), new Object());
        }
    }
}
