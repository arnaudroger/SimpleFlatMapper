# Benchmark results

## H2
```
java -jar sfm-benchmark/sfm-benchmark-jdbc/target/benchmarks.jar -bm avgt -tu us -f 1 -p db=H2
```

```
Benchmark                                  (db)  (limit)  Mode  Cnt   Score   Error  Units
o.s.jdbc.JdbcManualBenchmark.testQuery       H2        1  avgt   20   0.515 ± 0.021  us/op
o.s.jdbc.JdbcManualBenchmark.testQuery       H2       10  avgt   20   1.067 ± 0.025  us/op
o.s.jdbc.JdbcManualBenchmark.testQuery       H2      100  avgt   20   6.467 ± 0.330  us/op
o.s.jdbc.JdbcManualBenchmark.testQuery       H2     1000  avgt   20  60.188 ± 2.229  us/op
o.s.sfm.JdbcSfmDynamicBenchmark.testQuery    H2        1  avgt   20   0.722 ± 0.008  us/op
o.s.sfm.JdbcSfmDynamicBenchmark.testQuery    H2       10  avgt   20   1.490 ± 0.086  us/op
o.s.sfm.JdbcSfmDynamicBenchmark.testQuery    H2      100  avgt   20   8.418 ± 0.368  us/op
o.s.sfm.JdbcSfmDynamicBenchmark.testQuery    H2     1000  avgt   20  79.905 ± 1.876  us/op
o.s.sfm.JdbcSfmStaticBenchmark.testQuery     H2        1  avgt   20   0.546 ± 0.029  us/op
o.s.sfm.JdbcSfmStaticBenchmark.testQuery     H2       10  avgt   20   1.258 ± 0.056  us/op
o.s.sfm.JdbcSfmStaticBenchmark.testQuery     H2      100  avgt   20   8.176 ± 0.461  us/op
o.s.sfm.JdbcSfmStaticBenchmark.testQuery     H2     1000  avgt   20  81.148 ± 3.553  us/op
```

## MYSQL
```
java -jar sfm-benchmark/sfm-benchmark-jdbc/target/benchmarks.jar -bm avgt -tu us -f 1 -p db=MYSQL
```

```
Benchmark                                   (db)  (limit)  Mode  Cnt     Score    Error  Units
o.s.jdbc.JdbcManualBenchmark.testQuery     MYSQL        1  avgt   20    53.504 ±  2.069  us/op
o.s.jdbc.JdbcManualBenchmark.testQuery     MYSQL       10  avgt   20    63.635 ±  0.221  us/op
o.s.jdbc.JdbcManualBenchmark.testQuery     MYSQL      100  avgt   20   155.784 ±  0.518  us/op
o.s.jdbc.JdbcManualBenchmark.testQuery     MYSQL     1000  avgt   20  1001.851 ±  3.598  us/op
o.s.sfm.JdbcSfmDynamicBenchmark.testQuery  MYSQL        1  avgt   20    53.485 ±  0.246  us/op
o.s.sfm.JdbcSfmDynamicBenchmark.testQuery  MYSQL       10  avgt   20    65.627 ±  0.264  us/op
o.s.sfm.JdbcSfmDynamicBenchmark.testQuery  MYSQL      100  avgt   20   158.102 ±  0.369  us/op
o.s.sfm.JdbcSfmDynamicBenchmark.testQuery  MYSQL     1000  avgt   20  1018.097 ±  5.968  us/op
o.s.sfm.JdbcSfmStaticBenchmark.testQuery   MYSQL        1  avgt   20    53.066 ±  0.208  us/op
o.s.sfm.JdbcSfmStaticBenchmark.testQuery   MYSQL       10  avgt   20    63.268 ±  0.203  us/op
o.s.sfm.JdbcSfmStaticBenchmark.testQuery   MYSQL      100  avgt   20   160.577 ±  0.638  us/op
o.s.sfm.JdbcSfmStaticBenchmark.testQuery   MYSQL     1000  avgt   20  1022.683 ± 12.668  us/op
```