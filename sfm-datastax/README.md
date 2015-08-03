[![Maven Central](https://img.shields.io/maven-central/v/org.simpleflatmapper/sfm-datastax.svg)](https://maven-badges.herokuapp.com/maven-central/org.simpleflatmapper/sfm-datastax)
[![JavaDoc](https://img.shields.io/badge/javadoc-2.0-blue.svg)](http://www.javadoc.io/doc/org.simpleflatmapper/sfm-datastax)

# Datastax integration

## Add dependency

```xml
		<dependency>
			<groupId>org.simpleflatmapper</groupId>
			<artifactId>sfm-datastax</artifactId>
			<version>2.0</version>
		</dependency>
```

## Mapper

```java

    final DatastaxMapper<DbObject> mapper = DatastaxMapperFactory.newInstance().mapTo(DbObject.class);

    ...

        ResultSet rs = session.execute("select id, name, email, creation_time, type_ordinal, type_name from dbobjects");
        final Iterator<DbObject> iterator = mapper.iterator(rs);
```