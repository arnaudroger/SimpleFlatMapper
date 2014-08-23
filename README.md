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
```sql
create table MyTable {
	id bigint,
	email varchar(256),
	my_property int
}
```

```java
public class MyObject {
	private long id;
	public void setId(long id) { this.id = id; }
	public long getId() { return id; }
	
	private String email;
	public void setEmail(String email) { this.email = email; }
	public String getEmail() { return email; }
	
	private int myProperty;
	public void setMyProperty(int prop) { this.myProperty = prop; }
	public int getProperty() { return myProperty; }
	
	public String toString() { ... }
}

public class MyDao {
    private final JdbcMapper<MyObject> mapper = 
    	JdbcMapperFactory.newInstance().newMapper(MyObject.class);

    public void writeAllObjectTo(Writer writer, Connection conn) throws Exception {
        PreparedStatement ps = 
        	conn.prepareStatement("select id, email, my_property from MyTable");
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

Mapping
========

the Dynamic mapper will use assume a column name from the database will match the lower case property name appart from the underscore.

ie:
```
- my_property => myProperty
- myproperty => myProperty
```

Performance
========
See [src/test/benchmarks](/src/test/benchmarks) for more details.

Run 100000 queries against in memory hsqldb database, store the time it takes to read all the object in HdrHistogram. The table show the percentage above the pure jdbc benchmark 50% percentile. if it takes 100s for pure jdbc and 105s the targeted implementation it will show 5%.

SFM is for SimpleFlatMapper.

Please note that there is always a bit a variability in the results specially with 1 row. It's been between -2% and 4% so far for SFM Static/Dynamic.

|Nb Rows|SFM Static|SFM Dynamic|SFM Dynamic NoASM|Hibernate|MyBatis|
|------:|------:|-------:|-------:|------:|----:|
|1*|4%|4%|6%|181%|127%|
|10|1%|1%|12%|251%|228%|
|100|1%|2%|41%|512%|560%|
|1000|4%|4%|56%|675%|762%|
|10000|3%|5%|59%|718%|825%|

TODO
=======

Misc
-------
- Publish to sonatype
- Osgi support

Benchmark
------
- generate graphs

JDBCMapper
------
- Definition of custom mapping
- Inner object mapping
- List/Map mapping

CSVMapper
-----
- Csv Mapping



