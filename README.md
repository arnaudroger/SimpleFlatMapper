[![Build Status](https://travis-ci.org/arnaudroger/SimpleFlatMapper.svg?branch=master)](https://travis-ci.org/arnaudroger/SimpleFlatMapper)
[![Coverage Status](https://img.shields.io/coveralls/arnaudroger/SimpleFlatMapper.svg)](https://coveralls.io/r/arnaudroger/SimpleFlatMapper)

SimpleFlatMapper [v1.0.0b2](https://github.com/arnaudroger/SimpleFlatMapper/wiki/SimpleFlatMapper-v1.0.0b2)
========

[Google Group](https://groups.google.com/d/forum/simpleflatmapper)
Fast and Easy mapping from database and csv to POJO. 
A super lightweight no configuration ORM alternative to iBatis or Hibernate.

- Compatible with Java 6, 7 and 8. 
- [Flexible mapping](#inner-object-mapping) with inner object, arrays and list support
- Constructor, setter and field [injection](#value-injection)
- [Lambda Ready](src/main/java/org/sfm/jdbc)
- [Mapper for Jdbc ResultSet](src/main/java/org/sfm/jdbc)
- [Mapper for Csvs](src/main/java/org/sfm/csv)
- easy to integrate with [Spring JdbcTemplate](src/main/java/org/sfm/jdbc/spring). 
- [Mapper for Jooq](src/main/java/org/sfm/jooq)
- [Osgi](src/main/java/org/sfm/osgi) ready.
- [QueryDSL Jdbc support](src/main/java/org/sfm/querydsl)

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
|PureJdbc|1,884.74|4,981.15|35,108.09|333,855.37
|JdbcMapperStatic|1,961.92|5,241.82|37,552.89|357,953.28
|JdbcMapperStaticNoAsm|2,008.67|5,699.53|41,382.78|396,282.36
|JdbcMapperDynamic|2,212.21|6,238.83|45,468.29|437,271.11
|JdbcMapperDynamicNoAsm|2,236.20|6,602.97|49,683.90|480,556.26
|Roma|2,865.76|6,656.07|43,735.55|410,173.50
|Sql2O|6,469.54|12,008.03|65,871.85|603,808.79
|BeanPropertyRowMapper|14,789.95|118,763.24|1,141,809.37|11,418,948.31
|HibernateStatefull|19,554.63|40,889.14|252,239.35|2,524,391.76
|MyBatis|19,756.59|52,311.55|356,205.37|3,560,270.36
|JooqWithSfmMapping|32,604.51|43,735.90|145,061.77|1,160,997.02
|Jooq|40,174.46|55,876.53|208,156.68|1,809,146.26

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
|PureJdbc|245,113.75|317,283.50|667,585.28|2,865,698.12
|JdbcMapperStatic|247,535.57|316,710.25|690,418.35|2,872,758.13
|JdbcMapperStaticNoAsm|248,253.39|323,421.45|706,963.50|2,929,431.20
|JdbcMapperDynamic|248,192.67|330,782.38|710,853.21|2,982,768.31
|JdbcMapperDynamicNoAsm|247,403.30|331,048.68|711,389.78|3,038,683.13
|Roma|258,237.16|390,898.21|739,865.89|3,215,603.67
|Sql2O|262,418.64|388,297.26|779,157.67|3,601,053.19
|BeanPropertyRowMapper|313,128.79|474,301.33|2,001,145.59|14,377,744.63
|HibernateStatefull|349,046.36|409,242.83|1,116,423.86|5,625,843.02
|MyBatis|439,412.26|549,851.68|1,215,320.29|7,418,589.52
|JooqWithSfmMapping|348,793.42|443,529.22|872,743.17|4,052,582.49
|Jooq|354,027.51|462,085.84|974,488.90|4,579,035.61

% Difference from PureJdbc Average Time, the lower the better
![Mysql difference to PureJdbc](https://raw.githubusercontent.com/arnaudroger/orm-benchmark/master/src/main/resources/graphs/mysql-difference-from-purejdbc.png)

% Difference from PureJdbc Average Time Top5, the lower the better
![Mysql difference to PureJdbc Top5](https://raw.githubusercontent.com/arnaudroger/orm-benchmark/master/src/main/resources/graphs/mysql-difference-from-purejdbc-top5.png)

Csv Mapping
-------


Jmh benchmark. Average time in us to parse and map to object a csv file with 10, 1000 and 100000 rows.
The lower the better.

|Type/NbRows|10|1000|100,000|
|---------|---:|---:|---:|
|Sfm|4|304|31,703
|Jackson|6|485|50,774

Maven dependency
======

```xml
		<dependency>
			<groupId>com.github.arnaudroger</groupId>
			<artifactId>simpleFlatMapper</artifactId>
			<version>1.0.0b2</version>
		</dependency>
```

