[![Maven Central](https://img.shields.io/maven-central/v/org.simpleflatmapper/sfm-springjdbc.svg)](https://maven-badges.herokuapp.com/maven-central/org.simpleflatmapper/sfm-springjdbc)
[![JavaDoc](https://img.shields.io/badge/javadoc-2.13-blue.svg)](http://www.javadoc.io/doc/org.simpleflatmapper/sfm-springjdbc)

# Spring JDBC integration

## Add dependency

```xml
		<dependency>
			<groupId>org.simpleflatmapper</groupId>
			<artifactId>sfm-springjdbc</artifactId>
			<version>2.13</version>
		</dependency>
```

## Create parameterGetterMap

See [JdbcTemplateMapperFactoryTest](/sfm-springjdbc/src/test/java/org/simpleflatmapper/jdbc/spring/JdbcTemplateMapperFactoryTest.java) for more examples.

```java
class MyDao {
	private final RowMapper<DbObject> parameterGetterMap =
		JdbcTemplateMapperFactory.newInstance().newRowMapper(DbObject.class);

	public void doSomething() {
		List<DbObject> results = template.query(DbHelper.TEST_DB_OBJECT_QUERY, parameterGetterMap);
	}

	public void doSomethingElse() {
		 template
		 	.query(TEST_DB_OBJECT_QUERY,
		 		parameterGetterMap.newResultSetExtractor((o) -> System.out.println(o.toString())));
	}
}
```

## SqlParameterSource

```java
class MyDao {
	private final SqlParameterSourceFactory<DbObject> parameterSourceFactory =
		JdbcTemplateMapperFactory.newInstance().newSqlParameterSourceFactory(DbObject.class);

	public void insertObject(DbObject object) {
        template.update(
            "INSERT INTO DBOBJECTS(id, name, email) VALUES(:id, :name, :email)",
            parameterSourceFactory.newSqlParameterSource(object));

	}

	public void insertObjects(Collection<DbObject> objects) {
        template.batchUpdate(
            "INSERT INTO DBOBJECTS(id, name, email) VALUES(:id, :name, :email)",
            parameterSourceFactory.newSqlParameterSources(objects));
	}
}
```

## Crud

```java
class MyDao {

	JdbcTemplateCrud<DbObject, Long> objectCrud;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		objectCrud =
			JdbcTemplateMapperFactory
				.newInstance()
				.<DbObject, Long>crud(DbObject.class, Long.class)
				.to(template, "TEST_DB_OBJECT");
	}

	public void insertObject(DbObject object) {
        crud.create(object);
	}

	public void insertObjects(Collection<DbObject> objects) {
		crud.create(objects);
	}
}
```