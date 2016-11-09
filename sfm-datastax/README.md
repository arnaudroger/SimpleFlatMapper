[![Maven Central](https://img.shields.io/maven-central/v/org.simpleflatmapper/sfm-datastax.svg)](https://maven-badges.herokuapp.com/maven-central/org.simpleflatmapper/sfm-datastax)
[![JavaDoc](https://img.shields.io/badge/javadoc-blue.svg)](http://www.javadoc.io/doc/org.simpleflatmapper/sfm-datastax)

[Getting Started](http://simpleflatmapper.org/0103-getting-started-datastax.html)
# Datastax integration

Works with datastax 2.1.x to 3.0.x

## What about the Datastax Mapper
* SFM is at about 20% faster
* More flexible
* No configuration

## Mapper

```java

    final DatastaxMapper<DbObject> parameterGetterMap =
        DatastaxMapperFactory.newInstance().mapTo(DbObject.class);

    ...

        ResultSet rs =
            session.execute(
                "select id, name, email, creation_time, type_ordinal, type_name"
                + " from dbobjects");
        final Iterator<DbObject> iterator = parameterGetterMap.iterator(rs);


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
