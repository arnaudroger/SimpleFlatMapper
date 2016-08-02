package org.simpleflatmapper.utils;


import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.UUID;

/*
Benchmark                               Mode  Cnt         Score        Error  Units
UUIDBenchMarkRead.testByteBufferRead   thrpt   20  22906741.509 ± 267830.584  ops/s
UUIDBenchMarkRead.testBytesRead        thrpt   20  22196257.048 ± 949298.125  ops/s
UUIDBenchMarkWrite.testByteBufferRead  thrpt   20  22689803.554 ± 344674.334  ops/s
UUIDBenchMarkWrite.testBytesRead       thrpt   20  22728330.770 ± 542282.638  ops/s

 */
@State(Scope.Benchmark)
public class UUIDBenchMarkRead {



    byte[] bytes;

    @Setup(Level.Invocation)
    public void setUp() {
        bytes = UUIDRWBB.toBytes(UUID.randomUUID());
    }

    @Benchmark
    public UUID testByteBufferRead() {
        return UUIDRWBB.fromBytes(bytes);
    }


    @Benchmark
    public UUID testBytesRead() {
        return UUIDRWBS.fromBytes(bytes);
    }



}
