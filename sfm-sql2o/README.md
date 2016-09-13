[![Maven Central](https://img.shields.io/maven-central/v/org.simpleflatmapper/sfm-sql2o.svg)](https://maven-badges.herokuapp.com/maven-central/org.simpleflatmapper/sfm-sql2o)
[![JavaDoc](https://img.shields.io/badge/javadoc-2.13-blue.svg)](http://www.javadoc.io/doc/org.simpleflatmapper/sfm-sql2o)

# sql2o integration

## Add dependency

```xml
		<dependency>
			<groupId>org.simpleflatmapper</groupId>
			<artifactId>sfm-sql2o</artifactId>
			<version>2.13</version>
		</dependency>
```

## SQL2o integration

```java
    Query query = sql2o.open().createQuery("select * from table");
    query.setAutoDeriveColumnNames(true);
    query.setResultSetHandlerFactoryBuilder(new SfmResultSetHandlerFactoryBuilder());

    List<DbObject> dbObjects = query.executeAndFetch(DbObject.class);
```