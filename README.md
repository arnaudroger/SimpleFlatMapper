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

Ibatis and hibernate have very expensive injection mechanism. On the hsqldb in memory the markup for a medium size query is [400%](#in-mem-hsqldb) for both. 

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

Use JMH to with sample mode.

See [orm-benchmarks](https://github.com/arnaudroger/orm-benchmark) for more details.


Mock Connection
-------

|Benchmark|1|10|100|1000|
|---------|---:|---:|---:|---:|
|SfmStaticJmhBenchmarks|15.91%|44.47%|130.91%|181.19%|
|SfmDynamicJmhBenchmarks|26.57%|52.88%|133.69%|178.76%|
|SfmNoAsmJmhBenchmarks|44.90%|253.80%|766.06%|937.20%|
|RomaJmhBenchmarks|2.36%|31.93%|100.49%|139.69%|
|Sql2oJmhBenchmarks|986.62%|1331.76%|2274.64%|2571.55%|
|HibernateJmhBenchmarks|6829.42%|8785.59%|14259.63%|17404.53%|
|MyBatisJmhBenchmarks|4189.73%|10242.03%|24265.92%|30279.47%|

In mem HsqlDb
-------

|Benchmark|1|10|100|1000
|---------|---:|---:|---:|---:|
|SfmStaticJmhBenchmarks|2.40%|-0.48%|5.32%|8.46%|
|SfmDynamicJmhBenchmarks|4.67%|0.26%|6.40%|6.96%|
|SfmNoAsmJmhBenchmarks|2.17%|4.52%|22.62%|30.64%|
|RomaJmhBenchmarks|9.48%|10.03%|18.76%|19.68%|
|Sql2oJmhBenchmarks|44.42%|49.28%|63.63%|65.32%|
|HibernateJmhBenchmarks|189.69%|232.00%|400.30%|585.56%|
|MyBatisJmhBenchmarks|113.44%|217.05%|565.39%|836.84%|

Local Mysql
-------

|Benchmark|1|10|100|1000
|---------|---:|---:|---:|---:|
|SfmStaticJmhBenchmarks|0.15%|-0.15%|0.65%|1.16%|
|SfmDynamicJmhBenchmarks|2.01%|5.01%|0.62%|1.12%|
|SfmNoAsmJmhBenchmarks|2.22%|11.20%|2.53%|6.15%|
|RomaJmhBenchmarks|12.87%|48.74%|4.37%|5.56%|
|Sql2oJmhBenchmarks|20.57%|64.58%|12.04%|24.20%|
|HibernateJmhBenchmarks|143.21%|99.42%|57.94%|118.57%|
|MyBatisJmhBenchmarks|106.57%|78.53%|120.92%|207.32%|

Maven dependency
======

```xml
		<dependency>
			<groupId>com.github.arnaudroger</groupId>
			<artifactId>simpleFlatMapper</artifactId>
			<version>0.9.3</version>
		</dependency>
```

