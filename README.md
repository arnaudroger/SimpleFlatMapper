[![Build Status](https://travis-ci.org/arnaudroger/SimpleFlatMapper.svg?branch=master)](https://travis-ci.org/arnaudroger/SimpleFlatMapper)
[![Coverage Status](https://img.shields.io/coveralls/arnaudroger/SimpleFlatMapper.svg)](https://coveralls.io/r/arnaudroger/SimpleFlatMapper)

SimpleFlatMapper [v1.0.0b3](https://github.com/arnaudroger/SimpleFlatMapper/wiki/SimpleFlatMapper-v1.0.0b3)
========

Straight line to v1.0.0

[Google Group](https://groups.google.com/d/forum/simpleflatmapper)

Fast and Easy mapping from database and csv to POJO. 
A super lightweight no configuration ORM alternative to iBatis or Hibernate.

- Compatible with Java 6, 7 and 8. 
- [Flexible mapping](#inner-object-mapping) with inner object, arrays and list support
- Constructor, setter and field [injection](#value-injection)
- [Lambda Ready](src/main/java/org/sfm/jdbc), also exposes Streams
- [Mapper for Jdbc ResultSet](src/main/java/org/sfm/jdbc)
- [Mapper for Csvs](src/main/java/org/sfm/csv)
- easy to integrate with [Spring JdbcTemplate](src/main/java/org/sfm/jdbc/spring). 
- [Mapper for Jooq](src/main/java/org/sfm/jooq)
- [Osgi](src/main/java/org/sfm/osgi) ready.
- [QueryDSL Jdbc support](src/main/java/org/sfm/querydsl)
- Support JPA @Column for alias definition

Fastest CsvParser on [csv-parsers-comparaison](https://github.com/uniVocity/csv-parsers-comparison).

Fastest [CsvMapper](#csv-mapping) and [ORM Mapper](#in-mem-hsqldb), as far as I could test. If you believe something would be worth testing please raise an issue.

Feedbacks are more than welcome, don't hesitate to raise an issue or send me an email.

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

Sfm is very fast, the fastest I've been able to test again for the JdbcMapper and CsvMapper.

MyBatis, Hibernate, have a considerable overwrite, that can raise the cost by at least  [600%](#in-mem-hsqldb) on 1000 rows query.
BeanPropertyRowMapper is even worse.

### API intrusiveness

Ibatis provide the same kind of functionality but it forces you to use it's query mechanism and mask the jdbc api. 
Sfm just focus on the mapping from a [ResultSet](src/main/java/org/sfm/jdbc). You can manage the query the way you want. You can use [JdbcTemplate](src/main/java/org/sfm/jdbc/spring), even use it in an Hibernate session via the doWork method.


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

If your sub object has a one parameter constructor you can map the column name to the name of the subobject property. And that one does not require asm to do the constructor param naming.

```java
public class OuterObject {
	String id;
	MyObject subObject;
}

public class MyObject {
	public MyObject(String argNameNotMatter) {
	}
}
```

```sql
select id, subObject
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
Average time ns/op to execute, fetch and map to object the result of a query with 1, 10, 100 and 1000 rows.
The lower the better.

|Type/NbRows|1|10|100|1000|
|---------|---:|---:|---:|---:|
|PureJdbc|1,927.79|4,990.25|35,045.21|331,337.21
|JdbcMapperStatic|1,945.92|5,209.80|37,478.74|357,181.89
|JdbcMapperStaticNoAsm|1,992.03|5,632.44|41,012.90|397,116.84
|JdbcMapperDynamic|2,185.29|6,185.78|45,454.88|436,129.27
|JdbcMapperDynamicNoAsm|2,233.54|6,600.33|49,589.06|479,481.54
|Roma|2,837.18|6,642.74|43,826.42|410,034.25
|Sql2O|6,486.77|11,920.62|65,794.94|601,438.12
|HibernateStatefull|18,902.73|41,758.94|251,733.36|2,534,663.16
|JooqWithSfmMapping|32,990.96|44,402.42|143,848.83|1,174,061.05
|MyBatis|19,760.95|52,844.85|355,763.88|3,491,809.10
|Jooq|39,140.63|57,231.23|209,301.52|1,803,546.20
|BeanPropertyRowMapper|14,643.52|118,298.90|1,153,763.09|11,700,000.00

% Difference from PureJdbc Average Time, the lower the better
![HsqlDb Average time difference to PureJdbc](https://raw.githubusercontent.com/arnaudroger/orm-benchmark/master/src/main/resources/graphs/hsqldb-difference-from-purejdbc.png)

% Difference from PureJdbc Average Time Top 5, the lower the better
![HsqlDb Average time difference to PureJdbc Top5](https://raw.githubusercontent.com/arnaudroger/orm-benchmark/master/src/main/resources/graphs/hsqldb-difference-from-purejdbc-top5.png)

Local Mysql
-------
Average time ns/op to execute, fetch and map to object the result of a query with 1, 10, 100 and 1000 rows.
The lower the better.

|Type/NbRows|1|10|100|1000|
|---------|---:|---:|---:|---:|
|PureJdbc|244,467.47|284,516.96|622,006.45|2,381,662.80
|JdbcMapperStatic|243,904.51|280,482.25|594,999.27|2,400,891.93
|JdbcMapperStaticNoAsm|245,923.92|282,146.45|617,954.10|2,443,118.58
|Roma|253,717.67|333,795.64|629,789.26|2,476,847.22
|JdbcMapperDynamic|247,358.98|311,235.90|635,957.08|2,527,565.65
|JdbcMapperDynamicNoAsm|247,873.48|322,955.14|635,153.11|2,575,559.43
|Sql2O|261,462.68|352,085.79|712,857.30|2,894,537.87
|JooqWithSfmMapping|346,452.54|414,009.25|789,125.17|3,407,026.53
|Jooq|352,018.25|433,689.84|875,751.49|3,953,254.82
|HibernateStatefull|326,142.77|385,037.66|919,370.54|4,666,538.42
|MyBatis|425,368.59|512,035.00|956,222.21|5,293,635.82
|BeanPropertyRowMapper|320,750.60|471,385.88|1,841,829.36|13,630,933.78

% Difference from PureJdbc Average Time, the lower the better
![Mysql difference to PureJdbc](https://raw.githubusercontent.com/arnaudroger/orm-benchmark/master/src/main/resources/graphs/mysql-difference-from-purejdbc.png)

% Difference from PureJdbc Average Time Top5, the lower the better
![Mysql difference to PureJdbc Top5](https://raw.githubusercontent.com/arnaudroger/orm-benchmark/master/src/main/resources/graphs/mysql-difference-from-purejdbc-top5.png)

Csv Mapping
-------


Jmh benchmark. Average time in us to parse and map to object a csv file with 10, 1000 and 100000 rows.
The lower the better.


|Type/NbRows|1|10|100|1000|
|---------|---:|---:|---:|---:|
|JacksonCsvMapper|1110.39|8999.41|85186.40|787092.65
|ReadSfmCsvMapper|2335.10|6401.91|49774.74|512753.55
|OpenCsvMapper|2664.67|13828.84|127703.17|1291361.24

% Difference from Jackson Average Time, the lower the better
![Difference from Jackson](https://raw.githubusercontent.com/arnaudroger/orm-benchmark/master/src/main/resources/graphs/csv-java7.png)


Maven dependency
======

Java 8
----
```xml
		<dependency>
			<groupId>com.github.arnaudroger</groupId>
			<artifactId>simpleFlatMapper</artifactId>
			<version>1.0.0b3</version>
		</dependency>
```

Java 7
----
```xml
		<dependency>
			<groupId>com.github.arnaudroger</groupId>
			<artifactId>simpleFlatMapper</artifactId>
			<version>1.0.0b3</version>
			<classifier>jdk17</classifier>
		</dependency>
```

Java 6
----
```xml
		<dependency>
			<groupId>com.github.arnaudroger</groupId>
			<artifactId>simpleFlatMapper</artifactId>
			<version>1.0.0b3</version>
			<classifier>jdk16</classifier>
		</dependency>
```
