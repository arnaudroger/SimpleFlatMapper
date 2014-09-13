[![Build Status](https://travis-ci.org/arnaudroger/SimpleFlatMapper.svg?branch=master)](https://travis-ci.org/arnaudroger/SimpleFlatMapper)
[![Coverage Status](https://img.shields.io/coveralls/arnaudroger/SimpleFlatMapper.svg)](https://coveralls.io/r/arnaudroger/SimpleFlatMapper)

SimpleFlatMapper
========
Fast and Easy mapping from database to POJO. 
A super lightweight no configuration ORM alternative to iBatis or Hibernate.

Compatible with Java 6, 7 and 8. [Lambda Ready](#jdbcmapper) and easy to integrate with [Spring JdbcTemplate](#jdbctemplate). It is also [Osgi](#osgisupport) ready.

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

### Performance

Ibatis and hibernate have very expensive injection mechanism. On the hsqldb in memory the markup for a medium size query is [400%](#in-mem-hsqldb) for both. 
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

    public void writeAllObjectTo(Writer writer, Connection conn) throws SQLException {
        try (PreparedStatement ps = 
        		conn.prepareStatement("select id, email, my_property from MyTable")) {
	        try (ResultSet rs = ps.executeQuery()){
	            mapper.forEach(rs, (o) -> writer.append(o.toString()).append("\n"));
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
See [src/test/benchmarks](/src/test/benchmarks) for more details.

We run a query n times that return 1,10,100 and 1000 row against the [SmallBenchmarkObject](/src/test/java/org/sfm/beans/SmallBenchmarkObject.java) where n is

|Test|NB|
|------|------:|
|Mock|1000000|
|HsqlDB|500000|
|Mysql|100000|
 
We capture the time it takes from the query execution to the end of the transformation of all the rows into an [HdrHistogram](https://github.com/HdrHistogram/HdrHistogram). 
We capture from the query execution because some library don't give us control over that and the query exec time will be the same accross framework.


- SFM Static uses a predefined list of the columns
- SFM Dynamic will use the ResultSetMetadata
- SFM Dynamic NoASM will use the ResultSetMetadata but not use asm
- [Sql2o 1.5.1](http://www.sql2o.org)
- [Hibernate 4.3.6.Final](http://hibernate.org/)
- [MyBatis 3.2.7](http://mybatis.github.io/mybatis-3/)

Mock Connection
-------

|Nb Rows|SFM Static|SFM Dynamic|SFM Dynamic NoASM|Sql2o|Hibernate|MyBatis|
|------:|------:|-------:|-------:|------:|----:|----:|
|1|10%|16%|48%|1031%|6103%|4163%|
|10|8%|11%|200%|839%|3770%|7103%|
|100|2%|2%|325%|738%|1443%|10444%|
|1000|24%|59%|353%|706%|940%|11043%|

In mem HsqlDb
-------

|Nb Rows|SFM Static|SFM Dynamic|SFM Dynamic NoASM|Sql2o|Hibernate|MyBatis|
|------:|------:|-------:|-------:|------:|----:|----:|
|1|-1%|12%|2%|60%|167%|146%|
|10|4%|13%|8%|65%|231%|259%|
|100|4%|11%|19%|69%|394%|597%|
|1000|10%|16%|32%|77%|547%|829%|

Local Mysql
-------

|Nb Rows|SFM Static|SFM Dynamic|SFM Dynamic NoASM|Sql2o|Hibernate|MyBatis|
|------:|------:|-------:|-------:|------:|----:|----:|
|1|0%|2%|2%|9%|89%|127%|
|10|1%|2%|2%|139%|90%|131%|
|100|1%|1%|3%|11%|95%|75%|
|1000|2%|1%|6%|26%|127%|198%|


Maven dependency
======

```xml
		<dependency>
			<groupId>com.github.arnaudroger</groupId>
			<artifactId>simpleFlatMapper</artifactId>
			<version>0.9.2</version>
		</dependency>
```

