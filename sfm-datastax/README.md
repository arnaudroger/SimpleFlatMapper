[![Maven Central](https://img.shields.io/maven-central/v/org.simpleflatmapper/sfm-datastax.svg)](https://maven-badges.herokuapp.com/maven-central/org.simpleflatmapper/sfm-datastax)
[![JavaDoc](https://img.shields.io/badge/javadoc-2.14.1-blue.svg)](http://www.javadoc.io/doc/org.simpleflatmapper/sfm-datastax)

# Datastax integration

Works with datastax 2.1.x to 3.0.x

## What about the Datastax Mapper
* SFM is at about 20% faster
* More flexible
* No configuration

## Add dependency

```xml
		<dependency>
			<groupId>org.simpleflatmapper</groupId>
			<artifactId>sfm-datastax</artifactId>
			<version>2.14.1</version>
		</dependency>
```

## Mapper

```java

    final DatastaxMapper<DbObject> mapper =
        DatastaxMapperFactory.newInstance().mapTo(DbObject.class);

    ...

        ResultSet rs =
            session.execute(
                "select id, name, email, creation_time, type_ordinal, type_name"
                + " from dbobjects");
        final Iterator<DbObject> iterator = mapper.iterator(rs);


    final DatastaxBinder<DbObject> datastaxBinder =
        DatastaxMapperFactory.newInstance().mapFrom(DbObject.class);

    ...
        PreparedStatement preparedStatement = session.prepare(
           "insert into " +
           "dbobjects(id, name, email, creation_time, type_ordinal, type_name) " +
           "values(?, ?, ?, ?, ?, ?)"
        );

        session.execute(datastaxBinder.mapTo(dbObjects, preparedStatement));

```

## Crud

```java
    DatastaxCrud<DbObject, Long> crud =
        DatastaxMapperFactory.newInstance().crud(DbObject.class, Long.class).to(session, "dbobjects");


    crud.save(session, object);
    DbObject object = crud.read(session, object.getId());
    crud.delete(session, object.getId());

```
