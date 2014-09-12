package org.sfm.jdbc;

import static org.junit.Assert.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.Test;
import org.sfm.beans.Db1DeepObject;
import org.sfm.beans.Db2DeepObject;
import org.sfm.beans.DbFinal1DeepObject;
import org.sfm.utils.RowHandler;

public class JdbcMapperSubObjectTest {

	private static final String QUERY = "select 33 as id, "
			+ "'value' as value,  "
			+ "id as db_object_id, "
			+ "name as db_object_name, "
			+ "email as db_object_email, "
			+ "creation_time as db_object_creation_time, "
			+ "type_ordinal as db_object_type_ordinal, "
			+ "type_name as db_object_type_name "
			+ "from TEST_DB_OBJECT where id = 1 ";

	@Test
	public void testMapInnerObjectWithStaticMapper() throws Exception {
		ResultSetMapperBuilder<Db1DeepObject> builder = new ResultSetMapperBuilderImpl<Db1DeepObject>(Db1DeepObject.class);

		addColumns(builder);
		
		final JdbcMapper<Db1DeepObject> mapper = builder.mapper();
		
		DbHelper.testQuery(new RowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement t) throws Exception {
				ResultSet rs = t.executeQuery();
				rs.next();
				
				Db1DeepObject object = mapper.map(rs);
				assertEquals(33, object.getId());
				assertEquals("value", object.getValue());
				DbHelper.assertDbObjectMapping(object.getDbObject());
			}
		}, QUERY);
	}
	
	@Test
	public void testMapInnerObjectWithStaticMapperNamedColumn() throws Exception {
		ResultSetMapperBuilder<Db1DeepObject> builder = new ResultSetMapperBuilderImpl<Db1DeepObject>(Db1DeepObject.class);

		addNamedColumns(builder);
		
		final JdbcMapper<Db1DeepObject> mapper = builder.mapper();
		
		DbHelper.testQuery(new RowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement t) throws Exception {
				ResultSet rs = t.executeQuery();
				rs.next();
				
				Db1DeepObject object = mapper.map(rs);
				assertEquals(33, object.getId());
				assertEquals("value", object.getValue());
				DbHelper.assertDbObjectMapping(object.getDbObject());
			}
		}, QUERY);
	}
	
	@Test
	public void testMapInnerFinalObjectWithStaticMapper() throws Exception {
		ResultSetMapperBuilder<DbFinal1DeepObject> builder = new ResultSetMapperBuilderImpl<DbFinal1DeepObject>(DbFinal1DeepObject.class);

		addColumns(builder);
		
		final JdbcMapper<DbFinal1DeepObject> mapper = builder.mapper();
		
		DbHelper.testQuery(new RowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement t) throws Exception {
				ResultSet rs = t.executeQuery();
				rs.next();
				
				DbFinal1DeepObject object = mapper.map(rs);
				assertEquals(33, object.getId());
				assertEquals("value", object.getValue());
				DbHelper.assertDbObjectMapping(object.getDbObject());
			}
		}, QUERY);
	}

	@Test
	public void testMapInnerObject2LevelWithStaticMapper() throws Exception {
		final ResultSetMapperBuilder<Db2DeepObject> builder = new ResultSetMapperBuilderImpl<Db2DeepObject>(Db2DeepObject.class);

		DbHelper.testQuery(new RowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement t) throws Exception {
				ResultSet rs = t.executeQuery();
				
				JdbcMapper<Db2DeepObject> mapper = builder.addMapping(rs.getMetaData()).mapper();
				
				rs.next();
				
				Db2DeepObject object = mapper.map(rs);
				assertEquals(33, object.getId());
				assertEquals(32, object.getDb1Object().getId());
				assertEquals("value12", object.getDb1Object().getValue());
				DbHelper.assertDbObjectMapping(object.getDb1Object().getDbObject());
			}
		}, "select 33 as id, "
				+ "32 as db1_object_id,  "
				+ "'value12' as db1_object_value,  "
				+ "id as db1_object_db_object_id, "
				+ "name as db1_object_db_object_name, "
				+ "email as db1_object_db_object_email, "
				+ "creation_time as db1_object_db_object_creation_time, "
				+ "type_ordinal as db1_object_db_object_type_ordinal, "
				+ "type_name as db1_object_db_object_type_name "
				+ "from TEST_DB_OBJECT where id = 1 ");
	}
	
	public void addColumns(ResultSetMapperBuilder<?> builder) {
		builder.addIndexedColumn("id");
		builder.addIndexedColumn("value");
		builder.addIndexedColumn("db_object_id");
		builder.addIndexedColumn("db_object_name");
		builder.addIndexedColumn("db_object_email");
		builder.addIndexedColumn("db_object_creation_time");
		builder.addIndexedColumn("db_object_type_ordinal");
		builder.addIndexedColumn("db_object_type_name");
	}
	
	public void addNamedColumns(ResultSetMapperBuilder<?> builder) {
		builder.addNamedColumn("id");
		builder.addNamedColumn("value");
		builder.addNamedColumn("db_object_id");
		builder.addNamedColumn("db_object_name");
		builder.addNamedColumn("db_object_email");
		builder.addNamedColumn("db_object_creation_time");
		builder.addNamedColumn("db_object_type_ordinal");
		builder.addNamedColumn("db_object_type_name");
	}
}
