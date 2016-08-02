jOOQ
-----

See [JdbcTemplateMapperFactoryTest](/src/test/java/org/simpleflatmapper/jdbc/spring/JdbcTemplateMapperFactoryTest.java) for more examples.

```java
		DSLContext dsl = DSL
				.using(new DefaultConfiguration()
						.set(dataSource)
						.set(new SfmRecordMapperProvider()));
		
		...
		
		List<DbObject> list = dsl.select()
				.from("TEST_DB_OBJECT").fetchInto(DbObject.class);
```

