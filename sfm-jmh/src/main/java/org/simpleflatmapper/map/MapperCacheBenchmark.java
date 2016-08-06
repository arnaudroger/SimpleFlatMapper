package org.simpleflatmapper.map;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.map.mapper.MapperKey;

/*



 */
@State(Scope.Thread)
public class MapperCacheBenchmark {

    private int index;

    private ParamCache paramCache;

    private MapperKey<JdbcColumnKey> key;
    private IMapperCache<JdbcColumnKey, Object> mapperCache;

    public MapperCacheBenchmark( ){
    }
    @Setup
    public void setUp(ParamCache paramCache) {
        index = 0;
        this.paramCache = paramCache;
        mapperCache = paramCache.mapperCache;
    }

    @Setup(Level.Invocation)
    public void setUpInvocation() {
        key = paramCache.keys.get(index);
    }

    @TearDown(Level.Invocation)
    public void afterRun() {
        index ++;
        if (index >= paramCache.keys.size()) index = 0;
    }

    @Benchmark
    public Object testGet() {
        return mapperCache.get(key);
    }

}
