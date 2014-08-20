[![Build Status](https://travis-ci.org/arnaudroger/SimpleFlatMapper.svg?branch=master)](https://travis-ci.org/arnaudroger/SimpleFlatMapper)
[![Coverage Status](https://img.shields.io/coveralls/arnaudroger/SimpleFlatMapper.svg)](https://coveralls.io/r/arnaudroger/SimpleFlatMapper)

SimpleFlatMapper
========
Java library to map flat record - ResultSet, csv - to java object with no configuration and low footprint.

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
Run 100000 queries against in memory hsqldb database, store the time it takes to read all the object in HdrHistogram. The table show the percentage above the pure jdbc benchmark for different result size. if it takes 100s for pure jdbc and 105s the targeted implementation it will show 5%.

|Nb Rows|SFM Static|SFM Dynamic|Hibernate|MyBatis|
|------:|------:|-------:|-------:|------:|
|10|1%|3%|266%|235%|
|100|2%|3%|437%|559%|
|1000|4%|4%|564%|772%|
|10000|3%|2%|629%|856%|

TODO
=======
JDBCMapper
- Definition of custom mapping
- Inner object mapping
- List/Map mapping
CSVMapper
- Csv Mapping

