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
public class KeyObjectStorePutBenchmark {

    @Param(value = { "1", "10", "100", "1000", "10000"})
    public int nb;
    Object[][] data;
    @Setup
    public void setUpData() {
        data = new Object[nb][];
        for(int i = 0; i < data.length; i++) {
            data[i] = new Object[] { new MultiValueKey(new Object[] {new Object()}), new Object()};
        }
    }

    @Benchmark
    public Object testKeyObjectStore() {
        KeyObjectStore keyObjectStore = new KeyObjectStore();
        for(int i = 0; i < data.length; i++) {
            Key key = (Key) data[i][0];
            keyObjectStore.put(key, data[i][1]);
        }
        return keyObjectStore;
    }

    @Benchmark
    public Object testMap() {
        HashMap<Key, Object> keyObjectStore = new HashMap<Key, Object>();
        for(int i = 0; i < data.length; i++) {
            Key key = (Key) data[i][0];
            keyObjectStore.put(key, data[i][1]);
        }
        return keyObjectStore;
    }

}
