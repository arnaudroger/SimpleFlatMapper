package org.sfm.jdbc;

import org.junit.Test;
import org.sfm.beans.DbFinalListObject;
import org.sfm.beans.DbFinalObject;
import org.sfm.beans.DbObject;
import org.sfm.map.MappingException;
import org.sfm.map.impl.FieldMapper;
import org.sfm.map.impl.FieldMapperColumnDefinition;
import org.sfm.reflect.Getter;
import org.sfm.utils.RowHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcMapperCustomMappingTest {

	@Test
	public void testColumnAlias() throws SQLException, Exception {
		JdbcMapperFactory mapperFactory = JdbcMapperFactory.newInstance();
		mapperFactory.addAlias("not_id_column", "id");
		
		final JdbcMapper<DbObject> mapper = mapperFactory.newMapper(DbObject.class);
		
		DbHelper.testQuery(new RowHandler<PreparedStatement>() {

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
		
		final JdbcMapper<DbObject> mapper = JdbcMapperDbObjectTest.addColumn(mapperFactory.newBuilder(DbObject.class)).mapper();
		
		DbHelper.testQuery(new RowHandler<PreparedStatement>() {

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
		
		
		final JdbcMapper<DbObject> mapper = JdbcMapperDbObjectTest.addColumn(mapperFactory.newBuilder(DbObject.class)).mapper();
		
		DbHelper.testQuery(new RowHandler<PreparedStatement>() {

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
		
		DbHelper.testQuery(new RowHandler<PreparedStatement>() {

			@Override
			public void handle(PreparedStatement t) throws Exception {
				ResultSet r = t.executeQuery();
				r.next();
				DbHelper.assertDbObjectMapping(mapper.map(r));
			}
			
		}, DbHelper.TEST_DB_OBJECT_QUERY.replace("id,", "33 as id,"));
	}

	@Test
	public void testCustomReaderOnSetter() throws SQLException, Exception {
		JdbcMapperFactory mapperFactory = JdbcMapperFactory
				.newInstance().addCustomGetter("id", new Getter<ResultSet, Long>() {
					@Override
					public Long get(ResultSet target) throws Exception {
						return 1l;
					}
				});


		final JdbcMapper<DbObject> mapper = mapperFactory.newMapper(DbObject.class);

		DbHelper.testQuery(new RowHandler<PreparedStatement>() {

			@Override
			public void handle(PreparedStatement t) throws Exception {
				ResultSet r = t.executeQuery();
				r.next();
				DbHelper.assertDbObjectMapping(mapper.map(r));
			}

		}, DbHelper.TEST_DB_OBJECT_QUERY.replace("id,", "33 as id,"));
	}
	@SuppressWarnings("unchecked")
	@Test
	public void testCustomReaderOnSetterStatic() throws SQLException, Exception  {
		final JdbcMapper<DbObject> mapper =
				JdbcMapperFactory
						.newInstance()
						.newBuilder(DbObject.class)
						.addMapping("id",
								FieldMapperColumnDefinition.customGetter(new Getter() {
									@Override
									public Object get(Object target) throws Exception {
										return 1l;
									}
								}))
						.addMapping("name") //email, creation_time, type_ordinal, type_name
						.addMapping("email")
						.addMapping("creation_time")
						.addMapping("type_ordinal")
						.addMapping("type_name")
						.mapper();


		DbHelper.testQuery(new RowHandler<PreparedStatement>() {

			@Override
			public void handle(PreparedStatement t) throws Exception {
				ResultSet r = t.executeQuery();
				r.next();
				DbHelper.assertDbObjectMapping(mapper.map(r));
			}

		}, DbHelper.TEST_DB_OBJECT_QUERY.replace("id,", "33 as id,"));
	}
	@Test
	public void testCustomReaderOnConstructor() throws SQLException, Exception  {
		JdbcMapperFactory mapperFactory = JdbcMapperFactory
				.newInstance().addCustomGetter("id",new Getter<ResultSet, Long>() {
					@Override
					public Long get(ResultSet target) throws Exception {
						return 1l;
					}
				});


		final JdbcMapper<DbFinalObject> mapper = mapperFactory.newMapper(DbFinalObject.class);

		DbHelper.testQuery(new RowHandler<PreparedStatement>() {

			@Override
			public void handle(PreparedStatement t) throws Exception {
				ResultSet r = t.executeQuery();
				r.next();
				DbHelper.assertDbObjectMapping(mapper.map(r));
			}

		}, DbHelper.TEST_DB_OBJECT_QUERY.replace("id,", "33 as id,"));
	}
}