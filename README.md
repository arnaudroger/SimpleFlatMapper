[![Build Status](https://travis-ci.org/arnaudroger/SimpleFlatMapper.svg?branch=master)](https://travis-ci.org/arnaudroger/SimpleFlatMapper)
[![Coverage Status](https://img.shields.io/coveralls/arnaudroger/SimpleFlatMapper.svg)](https://coveralls.io/r/arnaudroger/SimpleFlatMapper)

SimpleFlatMapper
========
Fast and Easy mapping from jdbc, csv to POJO.

SFM aims to provide a solution to map from a flat record - ResultSet, csv - to a java object with no configuration and low runtime cost. The object analysis is done at the initialisation of the mapper eliminating later reflection cose. The query analysis is cached using a copy on write non locking technique. The cost of injection of data is reduce by generating asm for setters when possible and unrolling the mapping loop.

Design
========
- work by default/no configuration
- low foot print
- use plain jdbc
- no external library needed, asm supported for max perf

What it does not do
-------
- no relationship mapping
- no query generation
- no insert/update
- no caching
- no binary encoding

Samples
========

JdbcMapper
---------
```java
public class MyDao {
    private final JdbcMapper<MyObject> mapper = 
    	JdbcMapperFactory.newInstance().newMapper(MyObject.class);

    public void writeAllObjectTo(Writer writer, Connection conn) throws Exception {
        PreparedStatement ps = conn.prepareStatement("select * from table");
        try {
            ResultSet rs = ps.executeQuery();
            mapper.forEach(rs, 
                new Handler<MyObject>() {
                     void handle(MyObject object) {
                         writer.append(object.toString()).append("\n");
                     }
                });
        } finally {
            ps.close();
        }
    }
}
```

Performance
========
See [src/test/benchmarks](/src/test/benchmarks) for more details.

Run 100000 queries against in memory hsqldb database, store the time it takes to read all the object in HdrHistogram. The table show the percentage above the pure jdbc benchmark for different result size. if it takes 100s for pure jdbc and 105s the targeted implementation it will show 5%.

Please be aware that there will be a bit of noise.

|Nb Rows|SFM Static|SFM Dynamic|Hibernate|MyBatis|
|------:|------:|-------:|-------:|------:|
|10|2%|4%|264%|232%|
|100|1%|3%|487%|555%|
|1000|4%|6%|630%|777%|
|10000|3%|4%|626%|840%|

TODO
=======

Misc
-------
- Publish to sonatype
- Osgi support

Benchmark
------
- generate benchmark with new pure jdbc code
- generate graphs

JDBCMapper
------
- Definition of custom mapping
- Inner object mapping
- List/Map mapping

CSVMapper
-----
- Csv Mapping



