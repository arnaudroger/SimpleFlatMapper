[![Build Status](https://travis-ci.org/arnaudroger/SimpleFlatMapper.svg?branch=master)](https://travis-ci.org/arnaudroger/SimpleFlatMapper)
[![Coverage Status](https://img.shields.io/coveralls/arnaudroger/SimpleFlatMapper.svg)](https://coveralls.io/r/arnaudroger/SimpleFlatMapper)

SimpleFlatMapper [v1.0.0rc2](https://github.com/arnaudroger/SimpleFlatMapper/wiki/SimpleFlatMapper-v1.0.0rc2)
========

Next build should be 1.0.0

[Google Group](https://groups.google.com/d/forum/simpleflatmapper)

Fast and Easy mapping from database and csv to POJO. 
A super lightweight no configuration ORM alternative to iBatis or Hibernate.

- Compatible with Java 6, 7 and 8. 
- [Flexible mapping](#inner-object-mapping) with inner object, arrays and list support
- Constructor, setter and field [injection](#value-injection)
- Support for [Lambdas](src/main/java/org/sfm/jdbc) and Stream.
- [Mapper for Jdbc ResultSet](src/main/java/org/sfm/jdbc)
- [Mapper for Csvs](src/main/java/org/sfm/csv)
- easy to integrate with [Spring JdbcTemplate](src/main/java/org/sfm/jdbc/spring). 
- [Mapper for Jooq](src/main/java/org/sfm/jooq)
- [Osgi](src/main/java/org/sfm/osgi) service.
- [QueryDSL Jdbc support](src/main/java/org/sfm/querydsl)
- Support JPA @Column for alias definition

Fastest CsvParser on [csv-parsers-comparaison](https://github.com/uniVocity/csv-parsers-comparison).

Fastest [CsvMapper](#csv-mapping) and [ORM Mapper](#in-mem-hsqldb).

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

MyBatis, Hibernate, have a considerable overwrite, that can raise the cost by at least [600%](#in-mem-hsqldb) on 1000 rows query.
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
|PureJdbc|1,925.79|5,042.98|35,186.81|336,625.96
|JdbcMapperStatic|2,019.54|5,255.85|37,673.69|358,678.93
|JdbcMapperStaticNoAsm|2,015.28|5,544.55|41,696.29|399,234.51
|JdbcMapperDynamic|2,181.21|6,218.88|45,714.43|439,786.14
|Roma|2,634.56|6,455.94|43,483.63|410,553.17
|JdbcMapperDynamicNoAsm|2,214.56|6,671.09|49,871.66|480,406.45
|Sql2O|6,452.89|12,226.59|66,217.41|609,071.18
|Jooq.testSqlSmfMapper|32,822.27|43,472.96|141,448.46|1,118,003.03
|Jooq.testFetchRecord|29,460.89|44,009.94|176,350.97|1,559,655.25
|MyBatis|20,142.78|52,291.87|359,531.09|3,493,167.27
|Jooq.testSqlWithJooqMapper|38,604.32|55,232.34|204,774.29|1,772,289.92
|BeanPropertyRowMapper|15,024.78|121,918.09|1,191,479.77|11,839,887.44
|HibernateStatefull|37,067.58|143,725.44|1,339,112.69|12,231,137.68

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
|JdbcMapperDynamic|244,132.45|303,217.51|667,957.81|2,982,436.00
|PureJdbc|239,473.80|309,337.96|642,470.12|2,869,874.95
|JdbcMapperStatic|240,930.98|312,776.74|646,734.60|2,867,544.47
|JdbcMapperStaticNoAsm|241,145.05|315,291.14|661,614.65|2,913,358.80
|JdbcMapperDynamicNoAsm|243,952.93|321,778.82|682,339.77|3,003,549.52
|Roma|253,860.87|352,882.58|718,077.47|3,231,870.34
|Sql2O|260,788.27|368,675.83|776,665.14|3,625,665.28
|Jooq.testSqlSmfMapper|347,155.42|427,559.17|862,504.44|3,992,848.11
|Jooq.testFetchRecord|345,209.38|432,352.72|938,448.50|4,531,427.84
|Jooq.testSqlWithJooqMapper|350,431.61|438,528.91|892,823.06|4,042,104.61
|BeanPropertyRowMapper|304,083.26|466,785.52|1,998,807.89|14,649,609.80
|MyBatis|424,565.50|522,097.96|1,232,858.88|7,414,298.89
|HibernateStatefull|414,734.04|603,521.08|2,318,520.92|16,223,061.59

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
|JacksonCsvMapper|1,118.47|9,118.87|85,848.88|792,258.04
|ReadSfmCsvMapper|2,316.74|6,360.36|54,518.06|569,732.59
|OpenCsvMapper|2,602.02|13,782.47|127,311.26|1,330,882.55

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
			<version>1.0.0rc2</version>
		</dependency>
```

Java 7
----
```xml
		<dependency>
			<groupId>com.github.arnaudroger</groupId>
			<artifactId>simpleFlatMapper</artifactId>
			<version>1.0.0rc2</version>
			<classifier>jdk17</classifier>
		</dependency>
```

Java 6
----
```xml
		<dependency>
			<groupId>com.github.arnaudroger</groupId>
			<artifactId>simpleFlatMapper</artifactId>
			<version>1.0.0rc2</version>
			<classifier>jdk16</classifier>
		</dependency>
```
