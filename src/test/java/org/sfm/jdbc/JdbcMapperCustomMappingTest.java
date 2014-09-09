package org.sfm.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.map.FieldMapper;
import org.sfm.map.MappingException;
import org.sfm.utils.Handler;

public class JdbcMapperCustomMappingTest {

	@Test
	public void testColumnAlias() throws SQLException, Exception {
		JdbcMapperFactory mapperFactory = JdbcMapperFactory.newInstance();
		mapperFactory.addAlias("not_id_column", "id");
		
		final JdbcMapper<DbObject> mapper = mapperFactory.newMapper(DbObject.class);
		
		DbHelper.testQuery(new Handler<PreparedStatement>() {

			@Override
			public void handle(PreparedStatement t) throws Exception {
				ResultSet r = t.executeQuery();
				r.next();
				DbHelper.assertDbObjectMapping(mapper.map(r));
			}
			
		}, DbHelper.TEST_DB_OBJECT_QUERY.replace("id,", "id as not_id_column,"));
	}
	
	@Test
	public void testColumnAliasStatic() throws SQLException, Exception {
		JdbcMapperFactory mapperFactory = JdbcMapperFactory.newInstance();
		mapperFactory.addAlias("not_id_column", "id");
		
		final JdbcMapper<DbObject> mapper = JdbcMapperDbObjectTest.addNamedColumn(mapperFactory.newBuilder(DbObject.class)).mapper();
		
		DbHelper.testQuery(new Handler<PreparedStatement>() {

			@Override
			public void handle(PreparedStatement t) throws Exception {
				ResultSet r = t.executeQuery();
				r.next();
				DbHelper.assertDbObjectMapping(mapper.map(r));
			}
			
		}, DbHelper.TEST_DB_OBJECT_QUERY.replace("id,", "id as not_id_column,"));
	}
	
	@Test
	public void testCustomMappingStatic() throws SQLException, Exception  {
		JdbcMapperFactory mapperFactory = JdbcMapperFactory.newInstance();
		mapperFactory.addCustomFieldMapper("id", new FieldMapper<ResultSet, DbObject>() {
			@Override
			public void map(ResultSet source, DbObject target)
					throws MappingException {
				target.setId(1);
			}
		});
		
		
		final JdbcMapper<DbObject> mapper = JdbcMapperDbObjectTest.addNamedColumn(mapperFactory.newBuilder(DbObject.class)).mapper();
		
		DbHelper.testQuery(new Handler<PreparedStatement>() {

			@Override
			public void handle(PreparedStatement t) throws Exception {
				ResultSet r = t.executeQuery();
				r.next();
				DbHelper.assertDbObjectMapping(mapper.map(r));
			}
			
		}, DbHelper.TEST_DB_OBJECT_QUERY.replace("id,", "33 as id,"));
	}
	
	@Test
	public void testCustomMapping() throws SQLException, Exception  {
		JdbcMapperFactory mapperFactory = JdbcMapperFactory.newInstance();
		mapperFactory.addCustomFieldMapper("id", new FieldMapper<ResultSet, DbObject>() {
			@Override
			public void map(ResultSet source, DbObject target)
					throws MappingException {
				target.setId(1);
			}
		});
		
		
		final JdbcMapper<DbObject> mapper = mapperFactory.newMapper(DbObject.class);
		
		DbHelper.testQuery(new Handler<PreparedStatement>() {

			@Override
			public void handle(PreparedStatement t) throws Exception {
				ResultSet r = t.executeQuery();
				r.next();
				DbHelper.assertDbObjectMapping(mapper.map(r));
			}
			
		}, DbHelper.TEST_DB_OBJECT_QUERY.replace("id,", "33 as id,"));
	}
}