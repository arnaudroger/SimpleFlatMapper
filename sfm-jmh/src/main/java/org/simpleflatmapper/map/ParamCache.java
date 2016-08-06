package org.simpleflatmapper.map;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.map.mapper.MapperKey;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@State(Scope.Benchmark)
public class ParamCache {

    @Param(value = {"ARRAY", "SARRAY", "T2ARRAY",  "S2ARRAY", "TS2ARRAY", "CHM"})
    public CacheType cacheType;

    IMapperCache<JdbcColumnKey, Object> mapperCache;

    List<MapperKey<JdbcColumnKey>> keys;

    @Param("10")
    public int maxColumns;
    @Param({"1", "10", "50","100", "500", "2000"})
    public int size;

    @Setup
    public void setUp() {
        mapperCache = cacheType.newCache();
        keys = Utils.generateKeys(size, maxColumns);
        for (MapperKey<JdbcColumnKey> key : keys) {
            mapperCache.add(Utils.duplicateKey(key), new Object());
        }
    }

    public static void main( String[] args ) throws UnknownHostException
    {
        long start = System.currentTimeMillis();
        InetAddress localHost = InetAddress.getLocalHost();
        System.out.println(localHost);
        System.out.println(System.currentTimeMillis() - start);
    }
}
