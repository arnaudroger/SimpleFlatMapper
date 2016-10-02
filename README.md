[![License](https://img.shields.io/github/license/arnaudroger/simpleFlatMapper.svg)](https://raw.githubusercontent.com/arnaudroger/SimpleFlatMapper/master/LICENSE)
[![Build Status](https://img.shields.io/travis/arnaudroger/SimpleFlatMapper.svg)](https://travis-ci.org/arnaudroger/SimpleFlatMapper)
[![Coverage Status](https://img.shields.io/coveralls/arnaudroger/SimpleFlatMapper.svg)](https://coveralls.io/r/arnaudroger/SimpleFlatMapper)

[![Java 6](https://img.shields.io/badge/java-6-orange.svg)](#java-6)[![Java 7](https://img.shields.io/badge/java-7-green.svg)](#java-7)[![Java 8](https://img.shields.io/badge/java-8-brightgreen.svg)](#java-8)[![Java 9-ea](https://img.shields.io/badge/java-9-brightgreen.svg)](#java-8)

# [Simple Flat Mapper](http://simpleflatmapper.org/) 

## [Release Notes](https://github.com/arnaudroger/SimpleFlatMapper/wiki/SimpleFlatMapper-ReleaseNotes)

## [Getting Started](http://simpleflatmapper.org/01-getting-started.html) 

## Modules
| |
|------|-----|-----|
|[Csv](sfm-csv)|[![Maven Central](https://img.shields.io/maven-central/v/org.simpleflatmapper/sfm-csv.svg)](https://maven-badges.herokuapp.com/maven-central/org.simpleflatmapper/sfm-csv)|[![JavaDoc](https://img.shields.io/badge/javadoc-3.0-blue.svg)](http://www.javadoc.io/doc/org.simpleflatmapper/sfm-csv)
|[Jdbc](sfm-jdbc)|[![Maven Central](https://img.shields.io/maven-central/v/org.simpleflatmapper/sfm-jdbc.svg)](https://maven-badges.herokuapp.com/maven-central/org.simpleflatmapper/sfm-jdbc)|[![JavaDoc](https://img.shields.io/badge/javadoc-3.0-blue.svg)](http://www.javadoc.io/doc/org.simpleflatmapper/sfm-jdbc)
|[Cassandra Datastax driver](sfm-datastax)|[![Maven Central](https://img.shields.io/maven-central/v/org.simpleflatmapper/sfm-datastax.svg)](https://maven-badges.herokuapp.com/maven-central/org.simpleflatmapper/sfm-datastax)|[![JavaDoc](https://img.shields.io/badge/javadoc-3.0-blue.svg)](http://www.javadoc.io/doc/org.simpleflatmapper/sfm-datastax)
|[jOOQ](sfm-jooq)|[![Maven Central](https://img.shields.io/maven-central/v/org.simpleflatmapper/sfm-jooq.svg)](https://maven-badges.herokuapp.com/maven-central/org.simpleflatmapper/sfm-jooq)|[![JavaDoc](https://img.shields.io/badge/javadoc-3.0-blue.svg)](http://www.javadoc.io/doc/org.simpleflatmapper/sfm-jooq)
|[Poi Excel Spreadsheet](sfm-poi)|[![Maven Central](https://img.shields.io/maven-central/v/org.simpleflatmapper/sfm-poi.svg)](https://maven-badges.herokuapp.com/maven-central/org.simpleflatmapper/sfm-poi)|[![JavaDoc](https://img.shields.io/badge/javadoc-3.0-blue.svg)](http://www.javadoc.io/doc/org.simpleflatmapper/sfm-poi)
|[Query DSL](sfm-querydsl)|[![Maven Central](https://img.shields.io/maven-central/v/org.simpleflatmapper/sfm-querydsl.svg)](https://maven-badges.herokuapp.com/maven-central/org.simpleflatmapper/sfm-querydsl)|[![JavaDoc](https://img.shields.io/badge/javadoc-3.0-blue.svg)](http://www.javadoc.io/doc/org.simpleflatmapper/sfm-querydsl)
|[Spring JDBC](sfm-springjdbc)|[![Maven Central](https://img.shields.io/maven-central/v/org.simpleflatmapper/sfm-springjdbc.svg)](https://maven-badges.herokuapp.com/maven-central/org.simpleflatmapper/sfm-springjdbc)|[![JavaDoc](https://img.shields.io/badge/javadoc-3.0-blue.svg)](http://www.javadoc.io/doc/org.simpleflatmapper/sfm-springjdbc)
|[SQL2o](sfm-sql2o), no java6 |[![Maven Central](https://img.shields.io/maven-central/v/org.simpleflatmapper/sfm-sql2o.svg)](https://maven-badges.herokuapp.com/maven-central/org.simpleflatmapper/sfm-sql2o)|[![JavaDoc](https://img.shields.io/badge/javadoc-3.0-blue.svg)](http://www.javadoc.io/doc/org.simpleflatmapper/sfm-sql2o)


## blurb

SimpleFlatMapper is a library that provide
* [performant](https://github.com/arnaudroger/SimpleFlatMapper/wiki/Jdbc-Performance-Local-Mysql)
* easy to use
* flexible
mappers and abstraction to create pull mappers easily.

The [csv module](sfm-csv) provides support for
 * [CSV Parser](https://github.com/arnaudroger/SimpleFlatMapper/wiki/CsvParser)
 * [CSV Mapper](https://github.com/arnaudroger/SimpleFlatMapper/wiki/CsvParser#with-csvmapper)
 * [CSV Writter](https://github.com/arnaudroger/SimpleFlatMapper/wiki/CsvWriter)
 
The [jdbc module](sfm-jdbc)
 * [Jdbc Mapper](https://github.com/arnaudroger/SimpleFlatMapper/wiki/JdbcMapper)
 * [Jdbc Crud](https://github.com/arnaudroger/SimpleFlatMapper/wiki/Crud)

The CsvParser included is one of the [fastest](https://github.com/arnaudroger/SimpleFlatMapper/wiki/Csv-Performance) available in java.

There are also modules to support integration with external libraries

The mappers supports
 * Constructor injection
 * Method injection
 * Field injection
 * [Builder Pattern](https://github.com/arnaudroger/SimpleFlatMapper/wiki/Property-Mapping#builder-pattern) - like [Immutables](http://immutables.github.io/) -
 * Value Object support
 * ASM acceleration
 * Sub Object Mapping
 * Tuple support including [jOOL](https://github.com/jOOQ/jOOL) tuples ans [Fasttuple](https://github.com/boundary/fasttuple)
 * List and Array mapping
 * No configuration needed

See [Property Mapping Wiki](https://github.com/arnaudroger/SimpleFlatMapper/wiki/Property-Mapping) for more details.

If you encounter any problem with the mapping don't hesitate to [create an issue](https://github.com/arnaudroger/SimpleFlatMapper/issues/new).

## Building it

The build is using [Maven](http://maven.apache.org/).

```
git clone https://github.com/arnaudroger/SimpleFlatMapper.git
cd SimpleFlatMapper
mvn install
```

 
