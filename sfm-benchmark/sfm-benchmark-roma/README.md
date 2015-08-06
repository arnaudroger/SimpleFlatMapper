# Benchmark results

## H2
```
java -jar sfm-benchmark/sfm-benchmark-roma/target/benchmarks.jar -bm avgt -tu us -f 1 -p db=H2
```

```
Benchmark                                     (db)  (limit)  Mode  Cnt     Score     Error  Units
JdbcBeanPropertyRowMapperBenchmark.testQuery    H2        1  avgt   20     5.442 ±   0.213  us/op
JdbcBeanPropertyRowMapperBenchmark.testQuery    H2       10  avgt   20    40.923 ±   2.393  us/op
JdbcBeanPropertyRowMapperBenchmark.testQuery    H2      100  avgt   20   390.306 ±  21.443  us/op
JdbcBeanPropertyRowMapperBenchmark.testQuery    H2     1000  avgt   20  3819.675 ± 165.078  us/op
JdbcRomaBenchmark.testQuery                     H2        1  avgt   20     1.365 ±   0.046  us/op
JdbcRomaBenchmark.testQuery                     H2       10  avgt   20     2.610 ±   0.085  us/op
JdbcRomaBenchmark.testQuery                     H2      100  avgt   20    14.802 ±   0.423  us/op
JdbcRomaBenchmark.testQuery                     H2     1000  avgt   20   139.424 ±   4.282  us/op
```

## MYSQL

```
java -jar sfm-benchmark/sfm-benchmark-roma/target/benchmarks.jar -bm avgt -tu us -f 1 -p db=MYSQL
```

```
Benchmark                                      (db)  (limit)  Mode  Cnt     Score    Error  Units
JdbcBeanPropertyRowMapperBenchmark.testQuery  MYSQL        1  avgt   20    63.064 ±  1.429  us/op
JdbcBeanPropertyRowMapperBenchmark.testQuery  MYSQL       10  avgt   20   111.153 ±  2.577  us/op
JdbcBeanPropertyRowMapperBenchmark.testQuery  MYSQL      100  avgt   20   623.412 ± 12.933  us/op
JdbcBeanPropertyRowMapperBenchmark.testQuery  MYSQL     1000  avgt   20  4668.832 ± 87.666  us/op
JdbcRomaBenchmark.testQuery                   MYSQL        1  avgt   20    57.088 ±  0.245  us/op
JdbcRomaBenchmark.testQuery                   MYSQL       10  avgt   20    68.547 ±  0.384  us/op
JdbcRomaBenchmark.testQuery                   MYSQL      100  avgt   20   170.920 ±  1.241  us/op
JdbcRomaBenchmark.testQuery                   MYSQL     1000  avgt   20  1142.796 ±  5.645  us/op
```