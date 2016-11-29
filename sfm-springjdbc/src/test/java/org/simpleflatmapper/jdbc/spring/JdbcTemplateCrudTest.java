package org.simpleflatmapper.jdbc.spring;

import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.jdbc.ConnectedSelectQuery;
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

}
