[![Build Status](https://travis-ci.org/arnaudroger/SimpleFlatMapper.svg?branch=master)](https://travis-ci.org/arnaudroger/SimpleFlatMapper)
[![Coverage Status](https://img.shields.io/coveralls/arnaudroger/SimpleFlatMapper.svg)](https://coveralls.io/r/arnaudroger/SimpleFlatMapper)

[SimpleFlatMapper v0.9.11](https://github.com/arnaudroger/SimpleFlatMapper/wiki/SimpleFlatMapper-v0.9.11)
========
Fast and Easy mapping from database and csv to POJO. 
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

Sfm is very fast, the fastest I've been able to test again for the JdbcMapper and CsvMapper.

MyBatis, Hibernate, Jooq, QueryDSL have a considerable overwrite, that can raise the cost by at least  [600%](#in-mem-hsqldb) on 1000 rows query.
BeanPropertyRowMapper is even worse.

### API intrusiveness

Ibatis provide the same kind of functionality but it forces you to use it's query mechanism and mask the jdbc api. 
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

See [orm-benchmarks](https://github.com/arnaudroger/orm-benchmark) for benchmark sources and results.


In mem HsqlDb
-------

Average time us/op, the lower the better.

|Benchmark|1|10|100|1000|
|---------|---:|---:|---:|---:|
|PureJdbc|2.25|5.59|37.17|353.20
|JdbcMapperStatic|2.28|5.82|39.24|378.06
|JdbcMapperDynamic|2.63|6.77|47.93|459.36
|JdbcMapperDynamicNoAsm|2.71|7.23|52.28|505.37
|Roma|3.12|7.21|46.78|432.40
|Sql2o|7.33|13.06|68.86|613.32
|Jooq|40.73|57.24|212.12|1,800.72
|Hibernate|20.26|43.21|252.04|2,545.97
|MyBatis|21.05|54.17|359.39|3,491.73
|RowMapper|15.58|121.16|1,190.37|11,637.21

![HsqlDb us/op](https://raw.githubusercontent.com/arnaudroger/orm-benchmark/master/src/main/resources/graphs/hsqldb-all.png)

Local Mysql
-------
Average time us/op, the lower the better.

|Benchmark|1|10|100|1000|
|---------|---:|---:|---:|---:|
|PureJdbc|242.34|307.93|687.90|2,861.32
|JdbcMapperStatic|239.68|310.62|688.94|2,870.02
|JdbcMapperDynamic|241.73|319.13|693.55|2,958.79
|JdbcMapperDynamicNoAsm|241.06|313.13|693.64|3,023.33
|Roma|248.63|350.59|740.67|3,205.41
|Sql2o|257.37|368.18|814.33|3,575.64
|Jooq|349.60|456.55|1,014.53|4,646.25
|Hibernate|330.21|405.29|1,136.28|5,663.27
|MyBatis|429.10|533.00|1,246.27|7,503.65
|RowMapper|315.90|487.20|2,048.60|14,547.18

![Mysql us/op](https://raw.githubusercontent.com/arnaudroger/orm-benchmark/master/src/main/resources/graphs/mysql-all.png)

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
			<version>0.9.11</version>
		</dependency>
```

