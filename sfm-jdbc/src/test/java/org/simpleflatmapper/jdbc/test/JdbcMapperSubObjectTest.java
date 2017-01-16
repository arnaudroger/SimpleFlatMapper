package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperBuilder;
import org.simpleflatmapper.test.beans.Db1DeepObject;
import org.simpleflatmapper.test.beans.Db2DeepObject;
import org.simpleflatmapper.test.beans.DbFinal1DeepObject;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.test.jdbc.TestRowHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
		JdbcMapperBuilder<Db1DeepObject> builder = JdbcMapperFactoryHelper.asm().newBuilder(Db1DeepObject.class);

		addColumns(builder);
		
		final JdbcMapper<Db1DeepObject> mapper = builder.mapper();
		
		DbHelper.testQuery(new TestRowHandler<PreparedStatement>() {
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
    public void testMapInnerObjectWithColumnDefinition() throws Exception {
        JdbcMapperBuilder<Db1DeepObject> builder = JdbcMapperFactoryHelper.asm().newBuilder(Db1DeepObject.class);

        FieldMapperColumnDefinition<JdbcColumnKey> columnDefinition = FieldMapperColumnDefinition.customGetter(new Getter<ResultSet, String>() {
            @Override
            public String get(ResultSet target) throws Exception {
                return "ov1";
            }
        });
        builder.addMapping("db_object_name", columnDefinition);

        final JdbcMapper<Db1DeepObject> mapper = builder.mapper();

        ResultSet rs = mock(ResultSet.class);

        when(rs.getString(1)).thenReturn("name1");
        when(rs.next()).thenReturn(true, false);

        Db1DeepObject next = mapper.iterator(rs).next();

        assertEquals("ov1", next.getDbObject().getName());

    }

	@Test
	public void testMapInnerFinalObjectWithStaticMapper() throws Exception {
		JdbcMapperBuilder<DbFinal1DeepObject> builder = JdbcMapperFactoryHelper.asm().newBuilder(DbFinal1DeepObject.class);

		addColumns(builder);
		
		final JdbcMapper<DbFinal1DeepObject> mapper = builder.mapper();
		
		DbHelper.testQuery(new TestRowHandler<PreparedStatement>() {
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
		final JdbcMapperBuilder<Db2DeepObject> builder = JdbcMapperFactoryHelper.asm().newBuilder(Db2DeepObject.class);

		DbHelper.testQuery(new TestRowHandler<PreparedStatement>() {
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
	
	public void addColumns(JdbcMapperBuilder<?> builder) {
		builder.addMapping("id");
		builder.addMapping("value");
		builder.addMapping("db_object_id");
		builder.addMapping("db_object_name");
		builder.addMapping("db_object_email");
		builder.addMapping("db_object_creation_time");
		builder.addMapping("db_object_type_ordinal");
		builder.addMapping("db_object_type_name");
	}
	
}
