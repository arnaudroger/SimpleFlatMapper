[![Build Status](https://travis-ci.org/arnaudroger/SimpleFlatMapper.svg?branch=master)](https://travis-ci.org/arnaudroger/SimpleFlatMapper)
[![Coverage Status](https://img.shields.io/coveralls/arnaudroger/SimpleFlatMapper.svg)](https://coveralls.io/r/arnaudroger/SimpleFlatMapper)

SimpleFlatMapper
========
Fast and Easy mapping from database to POJO. 
A super lightweight no configuration ORM alternative to iBatis or Hibernate.

- Compatible with Java 6, 7 and 8. 
- [Lambda Ready](#jdbcmapper).
- easy to integrate with [Spring JdbcTemplate](#jdbctemplate). 
- [Osgi](#osgisupport) ready.
- [QueryDSL Jdbc support](#querydsl-jdbc)
- [CsvMapper](#csvmapper)

Feedbacks are welcome specially from real use case, don't hesitate to raise an issue or send me an email.

Design
========
- no configuration
- low foot print
- use plain jdbc
- no external library needed
- respect final fields
- support asm generation for max performance

What it does not do
-------
- no query generation
- no insert/update
- no caching
- no object lifecycle

Why?
-------

[Mapping Landscape](https://github.com/arnaudroger/SimpleFlatMapper/wiki/Mapping-Landscape)

### Performance

Mybatis and Hibernate have very expensive injection mechanism. On the hsqldb in memory the markup for a medium size query is [600%](#in-mem-hsqldb) for Hibernate and 800% for MyBatis. 

BeanPropertyRowMapper is very slow.

Sfm is as fast as it can using asm generation. Even if you don't use asm it is still a lot faster. 


### API intrusiveness

Ibatis provide the same kind of functionality put it forces you to use it's query mechanism and mask the jdbc api. 
Sfm just focus on the mapping from a [ResultSet](#jdbcmapper). You can manage the query the way you want. You can use [JdbcTemplate](#jdbctemplate), even use it in an Hibernate session via the doWork method.

Samples
========

JdbcMapper
---------

```java

public class MyDao {
    private final JdbcMapper<MyObject> mapper = 
    	JdbcMapperFactory.newInstance().newMapper(MyObject.class);
    public void printAllLambda(Writer writer, Connection conn) throws SQLException {
        try (PreparedStatement ps = 
        		conn.prepareStatement("select id, email, my_property from MyTable")) {
	        try (ResultSet rs = ps.executeQuery()){
	            mapper.forEach(rs, (o) -> writer.append(o.toString()).append("\n"));
	        }
        }
    }
    public void printAll(Writer writer, Connection conn) throws SQLException {
        try (PreparedStatement ps = 
        		conn.prepareStatement("select id, email, my_property from MyTable")) {
	        try (ResultSet rs = ps.executeQuery()){
	            mapper.forEach(rs, new RowHandler<MyObject>{
	            	public void handle(MyObject o) throws IOException { 
	            		writer.append(o.toString()).append("\n")); 
	            	}  
	            });
	        }
        }
    }
}
```

JdbcTemplate
-----

See [JdbcTemplateMapperFactoryTest](/src/test/java/org/sfm/jdbc/spring/JdbcTemplateMapperFactoryTest.java) for more examples.

```java
class MyDao {
	private final JdbcTemplateMapper<DbObject> mapper = 
		JdbcTemplateMapperFactory.newInstance().newMapper(DbObject.class);
		
	public void doSomething() {
		List<DbObject> results = template.query(DbHelper.TEST_DB_OBJECT_QUERY, mapper);
	}
	
	public void doSomethingElse() {
		 template
		 	.query(TEST_DB_OBJECT_QUERY, 
		 		mapper.newResultSetExtractor((o) -> System.out.println(o.toString())));
	}
}
```

OsgiSupport
------
The Osgi support just expose a service that will deal with the classloading wizardry needed to generate bytecode.

```java
class MyService {

	@Reference
	JdbcMapperService jdbcMapperService;
	
	volatile JdbcMapper mapper;
	
	@Activate
	public void activate() {
		mapper = jdbcMapperService.newFactory().newMapper(DbObject.class);
	}
}
```

QueryDSL Jdbc
------

```java
SQLQuery sqlquery = new SQLQueryImpl(conn, new HSQLDBTemplates());
try {
	return sqlquery
		.from(qTestDbObject)
		.where(qTestDbObject.id.eq(1l))
		.list(new QueryDslMappingProjection<DbObject>(DbObject.class, 
				qTestDbObject.id,
				qTestDbObject.name, 
				qTestDbObject.email, 
				qTestDbObject.creationTime, 
				qTestDbObject.typeName, 
				qTestDbObject.typeOrdinal ));
} finally {
	conn.close();
}
```

CsvMapper
---------

Uses header to match to the property.

```java
public class MyParser {
    private final CsvMapper<MyObject> mapper = 
    	CsvMapperFactory.newInstance().newMapper(MyObject.class);
    public void printAll(Writer writer, Reader reader) throws IOException {
        mapper.forEach(reader, (o) -> writer.append(o.toString()).append("\n"));
    }
}
```



Property Mapping
========

the mapper will assume a column name from the database will be matching the property name ignoring the case and underscores.

ie:
```
- my_property => myProperty
- myproperty => myProperty
```


Value Injection
------

The JdbcMapper supports
- constructor injection - needs asm to get the parameters name -
- setter injection
- field injection
It looks for injection on that order and if asm is present will generate optimised asm version.


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
```

Inner object mapping
-------

It also supports complex object injection via constructor, field or setter.

```java
public class OuterObject {
	String id;
	MyObject subObject;
}
```

```sql
select id, sub_object_id, sub_object_email, sub_object_my_property
```


List Mapping
-------

And list mapping in an object or at first level.

```java
public class ListObject {
	String id;
	List<MyObject> subObjects;
}
```

```sql
select id, 
	sub_objects_0_id, sub_objects_0_email, sub_objects_0_my_property, 
	sub_objects_1_id, 
	sub_objects_3_id
```

Performance
========

New Results, with better consistency. Use a slightly modified BoneCP and PreparedStatement cache. 
I will try to see how to get the static mapper closer to Roma by changing the error handling at field level.  
Because Roma assume that there is a 1 to 1 match between column and property it does not check for a column change between query. Roma is slower on the real database benchmark because it's using column name lookup.

See [orm-benchmarks](https://github.com/arnaudroger/orm-benchmark) for benchmark sources and results.


Mock Connection
-------

|Benchmark|1|10|100|1000|
|---------|---:|---:|---:|---:|
|JdbcMapperStatic|20%|132%|179%|194%
|JdbcMapperDynamic|123%|303%|438%|459%
|JdbcMapperDynamicNoAsm|216%|779%|1168%|1247%
|Roma|19%|164%|217%|244%
|Sql2o|4517%|3250%|3103%|2680%
|Hibernate|29318%|22365%|18036%|18318%
|MyBatis|18976%|25061%|28879%|30087%
|RowMapper|15585%|59746%|89662%|94740%

In mem HsqlDb
-------

|Benchmark|1|10|100|1000|
|---------|---:|---:|---:|---:|
|JdbcMapperStatic|2%|6%|5%|4%
|JdbcMapperDynamic|17%|24%|30%|30%
|JdbcMapperDynamicNoAsm|21%|43%|59%|59%
|Roma|39%|32%|25%|22%
|Sql2o|221%|135%|86%|74%
|Hibernate|808%|685%|582%|603%
|MyBatis|807%|870%|876%|878%
|RowMapper|565%|2072%|3014%|3080%

Local Mysql
-------

|Benchmark|1|10|100|1000|
|---------|---:|---:|---:|---:|
|JdbcMapperStatic|-1%|-1%|1%|1%
|JdbcMapperDynamic|0%|7%|8%|5%
|JdbcMapperDynamicNoAsm|1%|11%|11%|8%
|Roma|4%|17%|11%|12%
|Sql2o|8%|22%|22%|29%
|Hibernate|37%|30%|73%|99%
|MyBatis|76%|71%|87%|165%
|RowMapper|30%|59%|210%|417%

Csv Mapping 
-------


Jmh benchmark. The lower the better.

```
Benchmark                                      (nbRows)  Mode  Samples      Score  Score error  Units
o.s.b.c.CsvBenchmark.testJacksonCsvMapper            10  avgt      200      9.011        0.008  us/op
o.s.b.c.CsvBenchmark.testJacksonCsvMapper          1000  avgt      200    793.161        0.698  us/op
o.s.b.c.CsvBenchmark.testJacksonCsvMapper        100000  avgt      200  82052.639      145.173  us/op
o.s.b.c.CsvBenchmark.testReadSfmCsvMapper            10  avgt      200     17.999        0.011  us/op
o.s.b.c.CsvBenchmark.testReadSfmCsvMapper          1000  avgt      200    430.299        0.346  us/op
o.s.b.c.CsvBenchmark.testReadSfmCsvMapper        100000  avgt      200  47083.984      388.712  us/op

o.s.b.c.CsvBenchmark.testJacksonCsvParser            10  avgt      200     18.085        0.066  us/op
o.s.b.c.CsvBenchmark.testJacksonCsvParser          1000  avgt      200    606.611        1.146  us/op
o.s.b.c.CsvBenchmark.testJacksonCsvParser        100000  avgt      200  61537.596      107.770  us/op
o.s.b.c.CsvBenchmark.testSfmCsvParser                10  avgt      200     17.629        0.013  us/op
o.s.b.c.CsvBenchmark.testSfmCsvParser              1000  avgt      200    389.017        0.224  us/op
o.s.b.c.CsvBenchmark.testSfmCsvParser            100000  avgt      200  41065.558      144.704  us/op
o.s.b.c.CsvBenchmark.testUnivocityCsvParser          10  avgt      200   4666.659        7.167  us/op
o.s.b.c.CsvBenchmark.testUnivocityCsvParser        1000  avgt      200   5542.329       16.841  us/op
o.s.b.c.CsvBenchmark.testUnivocityCsvParser      100000  avgt      200  95024.513     1001.946  us/op
```

Maven dependency
======

```xml
		<dependency>
			<groupId>com.github.arnaudroger</groupId>
			<artifactId>simpleFlatMapper</artifactId>
			<version>0.9.9</version>
		</dependency>
```

