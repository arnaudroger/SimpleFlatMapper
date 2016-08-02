package org.simpleflatmapper.utils;


import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.UUID;

@State(Scope.Benchmark)
public class UUIDBenchMarkWrite {



    UUID uuid;

    @Setup(Level.Invocation)
    public void setUp() {
        uuid = UUID.randomUUID();
    }

    @Benchmark
    public byte[] testByteBufferRead() {
        return UUIDRWBB.toBytes(uuid);
    }


    @Benchmark
    public byte[] testBytesRead() {
        return UUIDRWBS.toBytes(uuid);
    }



}
