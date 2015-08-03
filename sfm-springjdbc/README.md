[![Maven Central](https://img.shields.io/maven-central/v/org.simpleflatmapper/sfm-springjdbc.svg)](https://maven-badges.herokuapp.com/maven-central/org.simpleflatmapper/sfm-springjdbc)
[![JavaDoc](https://img.shields.io/badge/javadoc-2.0-blue.svg)](http://www.javadoc.io/doc/org.simpleflatmapper/sfm-springjdbc)

# Spring JDBC integration

## Add dependency

```xml
		<dependency>
			<groupId>org.simpleflatmapper</groupId>
			<artifactId>sfm-springjdbc</artifactId>
			<version>2.0</version>
		</dependency>
```

## Create mapper

See [JdbcTemplateMapperFactoryTest](/src/test/java/org/sfm/jdbc/spring/JdbcTemplateMapperFactoryTest.java) for more examples.

```java
class MyDao {
	private final JdbcTemplateMapper<DbObject> mapper =
		JdbcTemplateMapperFactory.newInstance().newMapper(DbObject.class);

	public void doSomething() {
		List<DbObject> results = template.query(DbHelper.TEST_DB_OBJECT_QUERY, mapper);
	}

	public void doSomethingElse() {
		 template
		 	.query(TEST_DB_OBJECT_QUERY,
		 		mapper.newResultSetExtractor((o) -> System.out.println(o.toString())));
	}
}
```