[![Maven Central](https://img.shields.io/maven-central/v/org.simpleflatmapper/sfm-sql2o.svg)](https://maven-badges.herokuapp.com/maven-central/org.simpleflatmapper/sfm-sql2o)
[![JavaDoc](https://img.shields.io/badge/javadoc-blue.svg)](http://www.javadoc.io/doc/org.simpleflatmapper/sfm-sql2o)

[Getting Started](http://simpleflatmapper.org/0108-getting-started-sql2o.html)


## SQL2o integration

```java
    Query query = sql2o.open().createQuery("select * from table");
    query.setAutoDeriveColumnNames(true);
    query.setResultSetHandlerFactoryBuilder(new SfmResultSetHandlerFactoryBuilder());

    List<DbObject> dbObjects = query.executeAndFetch(DbObject.class);
```