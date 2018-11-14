package org.simpleflatmapper.jdbc.spring.test;

import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.jdbc.ConnectedSelectQuery;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateCrud;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.util.ListCollector;
import org.simpleflatmapper.util.CheckedConsumer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class JdbcTemplateCrudTest {
	
	JdbcTemplate template;
	
	@Before
	public void setUp() throws SQLException {
		Connection dbConnection;

		try {
			dbConnection = DbHelper.getDbConnection(DbHelper.TargetDB.MYSQL);
			dbConnection.createStatement().executeQuery("SELECT 1");
		} catch(Exception e) {
			dbConnection = DbHelper.getDbConnection(DbHelper.TargetDB.HSQLDB);
		}

		template = new JdbcTemplate(new SingleConnectionDataSource(dbConnection, true));
	}
	
	@Test
	public void testLazyCrud() throws SQLException {
		
		// drop table if exist
		try {
			template.execute("DROP TABLE TEST_DB_OBJECT_LAZY");
		} catch(Exception e) {
		}
		
		JdbcTemplateCrud<DbObject, Long> objectCrud =
				JdbcTemplateMapperFactory.newInstance()
						.<DbObject, Long>crud(DbObject.class, Long.class)
						.lazilyTo(template, "TEST_DB_OBJECT_LAZY");
		
		template.execute("create table TEST_DB_OBJECT_LAZY("
				+ " id bigint primary key,"
				+ " name varchar(100), "
				+ " email varchar(100),"
				+ " creation_Time timestamp, type_ordinal int, type_name varchar(10)  )");

		DbObject object = DbObject.newInstance();


		assertNull(objectCrud.read(object.getId()));

		// create
		Long key =
				objectCrud.create(object, new CheckedConsumer<Long>() {
					Long key;
					@Override
					public void accept(Long aLong) throws Exception {
						key = aLong;
					}
				}).key;

		assertNull(key);


		key = object.getId();
		// read
		assertEquals(object, objectCrud.read(key));



	}

	@Test
	public void testCrud() throws SQLException {
		JdbcTemplateCrud<DbObject, Long> objectCrud =
				JdbcTemplateMapperFactory.newInstance()
						.<DbObject, Long>crud(DbObject.class, Long.class).to(template, "TEST_DB_OBJECT");
		DbObject object = DbObject.newInstance();


		assertNull(objectCrud.read(object.getId()));

		// create
		Long key =
				objectCrud.create(object, new CheckedConsumer<Long>() {
					Long key;
					@Override
					public void accept(Long aLong) throws Exception {
						key = aLong;
					}
				}).key;

		assertNull(key);


		key = object.getId();
		// read
		assertEquals(object, objectCrud.read(key));

		object.setName("Udpdated");

		// update
		objectCrud.update(object);
		assertEquals(object, objectCrud.read(key));

		// delete
		objectCrud.delete(key);
		assertNull(objectCrud.read(key));

		objectCrud.create(object);
		assertEquals(object, objectCrud.read(key));
		objectCrud.delete(key);

		try {

			objectCrud.createOrUpdate(object);
			assertEquals(object, objectCrud.read(key));
		} catch(UnsupportedOperationException e) {}
	}


	@Test
	public void testCrudBatch() throws SQLException {
		JdbcTemplateCrud<DbObject, Long> objectCrud =
				JdbcTemplateMapperFactory.newInstance()
						.<DbObject, Long>crud(DbObject.class, Long.class).to(template, "TEST_DB_OBJECT");
		DbObject object = DbObject.newInstance();


		assertNull(objectCrud.read(object.getId()));

		// create
		final List<DbObject> objects = Arrays.asList(object);
		Long key =
				objectCrud.create(objects, new CheckedConsumer<Long>() {
					Long key;
					@Override
					public void accept(Long aLong) throws Exception {
						key = aLong;
					}
				}).key;

		assertNull(key);


		List<Long> keys = Arrays.asList(object.getId());
		// read
		assertEquals(objects, objectCrud.read(keys, new ListCollector<DbObject>()).getList());

		object.setName("Udpdated");

		// update
		objectCrud.update(objects);
		assertEquals(object, objectCrud.read(object.getId()));

		// delete
		objectCrud.delete(keys);
		assertNull(objectCrud.read(key));

		objectCrud.create(objects);
		assertEquals(objects, objectCrud.read(keys, new ListCollector<DbObject>()).getList());

		objectCrud.delete(keys);

		try {
			objectCrud.createOrUpdate(objects);
			assertEquals(objects, objectCrud.read(keys, new ListCollector<DbObject>()).getList());
		} catch (UnsupportedOperationException e) {}

	}

	@Test
	public void testWhere() throws SQLException {
		JdbcTemplateCrud<DbObject, Long> objectCrud =
				JdbcTemplateMapperFactory.newInstance()
						.<DbObject, Long>crud(DbObject.class, Long.class).to(template, "TEST_DB_OBJECT");
		DbObject object = DbObject.newInstance();


		ConnectedSelectQuery<DbObject, String> objectByName = objectCrud.where(" name = :name", String.class);

		assertNull(objectByName.readFirst(object.getName()));

		objectCrud.create(object);

		assertEquals(object, objectByName.readFirst(object.getName()));

		assertEquals(Arrays.asList(object), objectByName.read(object.getName(), new ListCollector<DbObject>()).getList());


	}
	
	@Test
	public void test530() {
		try {
			template.execute("DROP TABLE TEST_530");
		} catch(Exception e) {
		}

		template.execute("create table TEST_530("
				+ " id integer primary key, is_enabled boolean)");
		template.execute("insert into TEST_530 values(1, true)");
		
		JdbcTemplateCrud<O531, Integer> objectCrud =
				JdbcTemplateMapperFactory.newInstance()
						.<O531, Integer>crud(O531.class, Integer.class)
						.to(template, "TEST_530");
		
		assertNotNull(objectCrud);
	}

	public static class O531 {
		private final boolean isEnabled;
		private final int id;
		public O531(boolean isEnabled, int id) {
			this.isEnabled = isEnabled;
			this.id = id;
		}

		public boolean isEnabled() {
			return isEnabled;
		}

		public int getId() {
			return id;
		}
	}

}
