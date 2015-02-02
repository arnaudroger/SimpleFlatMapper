[![Build Status](https://travis-ci.org/arnaudroger/SimpleFlatMapper.svg?branch=master)](https://travis-ci.org/arnaudroger/SimpleFlatMapper)
[![Coverage Status](https://img.shields.io/coveralls/arnaudroger/SimpleFlatMapper.svg)](https://coveralls.io/r/arnaudroger/SimpleFlatMapper)

*bought simpleflatmapper.org the group id will be change accordingly in 1.5.0*

# Simple Flat Mapper (SFM) [v1.4.1](https://github.com/arnaudroger/SimpleFlatMapper/wiki/SimpleFlatMapper-v1.4.1)

SFM provides fast and easy to use mapper for

- [ResultSet aka micro ORM](https://github.com/arnaudroger/SimpleFlatMapper/wiki/JdbcMapper)
- [Csv](https://github.com/arnaudroger/SimpleFlatMapper/wiki/CsvParser#with-csvmapper)
- [Jooq and Sql2o](https://github.com/arnaudroger/SimpleFlatMapper/wiki/Sfm-integration-in-Jooq-And-Sql2o-for-performance) for easy performance gain.
- [SpringJdbcTemplate](src/main/java/org/sfm/jdbc/spring)
- [QueryDSL](src/main/java/org/sfm/querydsl)

It also provides one of the fastest [csv parser](https://github.com/arnaudroger/SimpleFlatMapper/wiki/CsvParser) available [See CsvParserComparaison fork](https://github.com/arnaudroger/csv-parsers-comparison).

The API is lambda friendly and the java 8 jars expose the mapped in form of Stream.

## Why?

SFM focuses on simplicity of use and performance. Current ORM Mapping solution are intrusive and/or quite slow -
can easily double your retrieval time in prod like setups, and Hibernate cache won't save you either because it
still needs to inflate the object from a tuple.

SFM is a library and does not force a framework on you it plugs on top of jdbc, jooq, java io, spring jdbc.

SFM makes also the assumption that it should be able to figure out the mapping rule without you being explicit about it.
Object will most of the time match the structure of a query or a csv. But it still allow you to customized the mapping
definition. [More details.](https://github.com/arnaudroger/SimpleFlatMapper/wiki/Property-Mapping)

SFM also supports Constructor injection and respect the semantic of final fields.

[More storyfied Why.](https://github.com/arnaudroger/SimpleFlatMapper/wiki/Why-extended-version)

## How Fast?

The Jdbc Mapper is the fastest on the market. Adding a maximum of 5% over pure jdbc on a query to a local Mysql. The next
fastest would be Roma that add between 10 and 15%. MyBatis and Hibernate adds more that 70% climbing quickly to 150%
for bigger queries.

The Csv Mapper is about 30% faster than jackson-csv.

The Jooq integration give you a mapping to object for almost no cost.

[More details.](https://github.com/arnaudroger/SimpleFlatMapper/wiki/Performance-Java-7)

## Getting Started

### Binaries

The binaries are available in maven central. There is a build for

#### Java 8

```xml
		<dependency>
			<groupId>com.github.arnaudroger</groupId>
			<artifactId>simpleFlatMapper</artifactId>
			<version>1.4.1</version>
		</dependency>
```

#### Java 7

```xml
		<dependency>
			<groupId>com.github.arnaudroger</groupId>
			<artifactId>simpleFlatMapper</artifactId>
			<version>1.4.1</version>
			<classifier>jdk17</classifier>
		</dependency>
```

#### Java 6

```xml
		<dependency>
			<groupId>com.github.arnaudroger</groupId>
			<artifactId>simpleFlatMapper</artifactId>
			<version>1.4.1</version>
			<classifier>jdk16</classifier>
		</dependency>
```

### Quick Samples

The mapper are all thread safe, and it is recommended to instantiate one per class to map as most of the work is done on
instantiation.

Those samples show how to build a list from the forEach method. These is not the recommended way of processing the
stream of data. As much as you can you would need to define your own RowHandler and do the work in there.

#### Jdbc

```java
public class MyDao {
	JdbcMapper<MyObject> mapper = JdbcMapperFactory.newInstance().newMapper(MyObject.class);

	public List<MyObject> findAll() throws SQLException {
		try (Connection conn = getConnection();
		     PreparedStatement ps = conn.prepareStatement("select * from my_table");
		     ResultSet rs = ps.executeQuery();) {
			return mapper.forEach(rs, new ListHandler<MyObject>()).getList();
		}
	}

}
```

#### Csv

```java
public class MyDao {
	CsvMapper<MyObject> mapper = CsvMapperFactory.newInstance().newMapper(MyObject.class);

	public List<MyObject> findAll(File file) throws SQLException {
		try (FileReader reader = new FileReader(file)) {
			return mapper.forEach(reader, new ListHandler<MyObject>()).getList();
		}
	}

}
```

### Building it

The build is using [Maven](http://maven.apache.org/).

```
git clone https://github.com/arnaudroger/SimpleFlatMapper.git
cd SimpleFlatMapper
mvn install
```
