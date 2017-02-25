package org.simpleflatmapper.map;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.simpleflatmapper.map.context.Key;
import org.simpleflatmapper.map.context.impl.KeyObjectStore;
import org.simpleflatmapper.map.context.impl.MultiValueKey;

import java.util.HashMap;

@State(Scope.Benchmark)
public class KeyObjectStoreGetBenchmark {

    @Param(value = { "1", "10", "100", "1000", "10000"})
    public int nb;

    KeyObjectStore keyObjectStore = new KeyObjectStore();
    HashMap<Key, Object> map = new HashMap<Key, Object>();
    Key[] keys;
    @Setup
    public void setUpData() {
        keys = new Key[nb];
        for(int i = 0; i < nb; i++) {
            MultiValueKey key = new MultiValueKey(new Object[]{new Object()});
            Object value = new Object();
            keys[i] = key;
            keyObjectStore.put(key, value);
            map.put(key, value);
        }
    }

    @Benchmark
    public void testKeyObjectStore(Blackhole blackhole) {
        for(int i = 0; i < keys.length; i++) {
            blackhole.consume(keyObjectStore.get(keys[i]));
        }
    }

    @Benchmark
    public void testMap(Blackhole blackhole) {
        for(int i = 0; i < keys.length; i++) {
            blackhole.consume(map.get(keys[i]));
        }
    }

}
