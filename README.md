[![Build Status](https://travis-ci.org/arnaudroger/SimpleFlatMapper.svg?branch=master)](https://travis-ci.org/arnaudroger/SimpleFlatMapper)
[![Coverage Status](https://img.shields.io/coveralls/arnaudroger/SimpleFlatMapper.svg)](https://coveralls.io/r/arnaudroger/SimpleFlatMapper)

SimpleFlatMapper
========
Fast and Easy mapping from jdbc, csv to POJO.

SFM aims to provide a solution to map from a flat record - ResultSet, csv - to a java object with no configuration and low runtime cost. The object analysis is done at the initialisation of the mapper eliminating later reflection cost. 

The JdbcMapper query analysis is cached using a copy on write non locking technique. The cost of injection of data is reduce by generating asm for setters when possible and unrolling the mapping loop.

The SimpleFlatMapper JdbcMapper is a lightweight alternative to ibatis and hibernate.

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

Why?
-------

### Performance

Ibatis and hibernate have very expensive injection mechanism. On the hsqldb in mem for a medium size query it's a about 700% mark up for both. 
Sfm get's as fast as it can using asm generation. Event if you don't use asm is still a lot faster. 

### API intrusiveness

Ibatis provide the same kind of functionality put it forces you to use it's query mechanism and mask the jdbc api. 
Sfm just focus on the mapping from a ResultSet. You can manage the query the way you want. You can use JdbcTemplate, even use it in an Hibernate session.

Performance
========
See [src/test/benchmarks](/src/test/benchmarks) for more details.

Run 100000 queries, store the time it takes to read all the object in HdrHistogram. The table show the percentage above the pure jdbc benchmark 50% percentile. if it takes 100s for pure jdbc and 105s the targeted implementation it will show 5%.

SFM is for SimpleFlatMapper.

As you can see by the negative numbers are the one that look a bit sour there can be quite a bit of noise in the benchmark I'm still looking at sorting that out.

Mock Connection
-------

|Nb Rows|SFM Static|SFM Dynamic|SFM Dynamic NoASM|Sql2o|Hibernate|MyBatis|
|------:|------:|-------:|-------:|------:|----:|----:|
|1|6%|15%|86%|923%|6053%|4330%|
|10|6%|13%|397%|817%|3774%|7731%|
|100|2%|0%|702%|733%|1484%|11252%|
|1000|24%|7%|789%|708%|978%|11922%|

In mem HsqlDb
-------

|Nb Rows|SFM Static|SFM Dynamic|SFM Dynamic NoASM|Sql2o|Hibernate|MyBatis|
|------:|------:|-------:|-------:|------:|----:|----:|
|1|2%|3%|10%|43%|171%|115%|
|10|3%|5%|16%|46%|223%|215%|
|100|3%|5%|43%|66%|386%|552%|
|1000|4%|5%|60%|73%|500%|758%|

Local Mysql
-------

|Nb Rows|SFM Static|SFM Dynamic|SFM Dynamic NoASM|Sql2o|Hibernate|MyBatis|
|------:|------:|-------:|-------:|------:|----:|----:|
|1|1%|3%|3%|9%|91%|127%|
|10|1%|2%|35%|140%|89%|131%|
|100|1%|1%|4%|12%|94%|77%|
|1000|3%|2%|12%|27%|124%|207%|

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
	private final long id;
	private final String email;
	private final int myProperty;
	
	public MyObject(long id, String email,  int myProperty) {
		this.id = id;
		this.email = email;
		this.myProperty = myProperty;
	}

	public long getId() { return id; }
	public String getEmail() { return email; }
	public int getProperty() { return myProperty; }
	
	public String toString() { ... }
}

public class MyDao {
    private final JdbcMapper<MyObject> mapper = 
    	JdbcMapperFactory.newInstance().newMapper(MyObject.class);

    public void writeAllObjectTo(Writer writer, Connection conn) throws SQLException {
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

JdbcTemplate
-----

```java
class MyDao {
	private final RowMapper<DbObject> mapper = new RowMapperFactory().newMapper(DbObject.class);
		
	public void doSomething() {		
		List<DbObject> results = template.query(DbHelper.TEST_DB_OBJECT_QUERY, mapper);
	}
}
```

OsgiSupport
------
class MyService {

	@Reference
	JdbcMapperService jdbcMapperService;
	
	JdbcMapper mapper;
	
	....
	
	@Activate
	public void activate() {
		mapper = jdbcMapperService.newFactory().newMapper(DbObject.class);
	}
}

Mapping
========

the Dynamic mapper will assume a column name from the database will be matching ignoring case and underscore to the property name.
It is on the todo list to be able to specify manual mapping and a custom property name matcher.

ie:
```
- my_property => myProperty
- myproperty => myProperty
```

Value Injection
=======

The JdbcMapper supports
- constructor injection using asm to get the parameter name
- setter injection
- field injection
- complex object injection

It looks for injection on that order and if asm is present will generate optimised asm version.

Maven dependency
======

```xml
		<dependency>
			<groupId>com.github.arnaudroger</groupId>
			<artifactId>simpleFlatMapper</artifactId>
			<version>0.5</version>
		</dependency>
```

TODO
=======


JDBCMapper
------
- [Definition of custom mapping](https://github.com/arnaudroger/SimpleFlatMapper/issues/2)
- [List mapping](https://github.com/arnaudroger/SimpleFlatMapper/issues/4)

CSVMapper
-----
- [Csv Mapping](https://github.com/arnaudroger/SimpleFlatMapper/issues/5)
 


