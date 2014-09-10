package org.sfm.jdbc;

import static org.junit.Assert.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.junit.Test;
import org.sfm.beans.DbFinalListObject;
import org.sfm.beans.DbListObject;
import org.sfm.beans.DbListOfString;
import org.sfm.beans.DbObject;
import org.sfm.reflect.ReflectionService;
import org.sfm.utils.RowHandler;

public class JdbcMapperListTest {
	
	private static final class TestDbFinalListObject implements
			RowHandler<PreparedStatement> {

		private final boolean asm;

		public TestDbFinalListObject(boolean asm) {
			this.asm = asm;
		}

		@Override
		public void handle(PreparedStatement t) throws Exception {
			ResultSet rs = t.executeQuery();
			final JdbcMapper< DbFinalListObject> mapper = new ResultSetMapperBuilderImpl<DbFinalListObject>(
					DbFinalListObject.class, new ReflectionService(true, asm))
					.addMapping(rs.getMetaData()).mapper();

			rs.next();

			DbFinalListObject object = mapper.map(rs);

			assertEquals(12, object.getId());
			assertEquals(3, object.getObjects().size());
			assertNull(object.getObjects().get(0));
			DbHelper.assertDbObjectMapping(object.getObjects().get(1));
			DbHelper.assertDbObjectMapping(object.getObjects().get(2));
		}
	}


	private static final class TestDbListObject implements
			RowHandler<PreparedStatement> {
		
		private final boolean asm ;
		
		public TestDbListObject(boolean asm) {
			this.asm = asm;
		}

		@Override
		public void handle(PreparedStatement t) throws Exception {
			ResultSet rs = t.executeQuery();
			final JdbcMapper<DbListObject> mapper =  
					new ResultSetMapperBuilderImpl<DbListObject>(DbListObject.class, new ReflectionService(true, asm))
						.addMapping(rs.getMetaData())
						.mapper();
			
			rs.next();
			
			DbListObject object = mapper.map(rs);
			
			assertEquals(12, object.getId());
			assertEquals(3, object.getObjects().size());
			assertNull(object.getObjects().get(0));
			DbHelper.assertDbObjectMapping(object.getObjects().get(1));
			DbHelper.assertDbObjectMapping(object.getObjects().get(2));
		}
	}


	private static final class TestListObject implements
			RowHandler<PreparedStatement> {
		
		private final boolean asm ;
		private List<DbObject> list;
		public TestListObject(boolean asm) {
			this.asm = asm;
		}

		@Override
		public void handle(PreparedStatement t) throws Exception {
			ResultSet rs = t.executeQuery();
			final JdbcMapper<List<DbObject>> mapper =  
					new ResultSetMapperBuilderImpl<List<DbObject>>(getClass().getDeclaredField("list").getGenericType(), 
							new ReflectionService(true, asm))
						.addMapping(rs.getMetaData())
						.mapper();
			
			rs.next();
			
			list = mapper.map(rs);
			
			assertEquals(3, list.size());
			assertNull(list.get(0));
			DbHelper.assertDbObjectMapping(list.get(1));
			DbHelper.assertDbObjectMapping(list.get(2));
		}
	}
	
	private static final class TestDbListString implements
			RowHandler<PreparedStatement> {

		private final boolean asm;

		public TestDbListString(boolean asm) {
			this.asm = asm;
		}

		@Override
		public void handle(PreparedStatement t) throws Exception {
			ResultSet rs = t.executeQuery();
			final JdbcMapper<DbListOfString> mapper = new ResultSetMapperBuilderImpl<DbListOfString>(
					DbListOfString.class, new ReflectionService(true, asm))
					.addMapping(rs.getMetaData()).mapper();

			rs.next();

			DbListOfString object = mapper.map(rs);

			assertEquals(12, object.getId());
			assertEquals(3, object.getObjects().size());
			assertNull(object.getObjects().get(0));
			assertEquals("value1", object.getObjects().get(1));
			assertEquals("value2", object.getObjects().get(2));
		}
	}
	
	private static final String QUERY = "select 12 as id, "
			+ " 1 as objects_1_id, 'name 1' as objects_1_name, 'name1@mail.com' as objects_1_email, TIMESTAMP'2014-03-04 11:10:03' as objects_1_creation_time, 2 as objects_1_type_ordinal, 'type4' as objects_1_type_name, "
			+ " 1 as objects_2_id, 'name 1' as objects_2_name, 'name1@mail.com' as objects_2_email, TIMESTAMP'2014-03-04 11:10:03' as objects_2_creation_time, 2 as objects_2_type_ordinal, 'type4' as objects_2_type_name "
			+ " from TEST_DB_OBJECT ";
	
	private static final String QUERY_LIST = "select "
			+ " 1 as objects_1_id, 'name 1' as objects_1_name, 'name1@mail.com' as objects_1_email, TIMESTAMP'2014-03-04 11:10:03' as objects_1_creation_time, 2 as objects_1_type_ordinal, 'type4' as objects_1_type_name, "
			+ " 1 as objects_2_id, 'name 1' as objects_2_name, 'name1@mail.com' as objects_2_email, TIMESTAMP'2014-03-04 11:10:03' as objects_2_creation_time, 2 as objects_2_type_ordinal, 'type4' as objects_2_type_name "
			+ " from TEST_DB_OBJECT ";
	
	private static final String QUERY_STRING_LIST = "select 12 as id, "
			+ " 'value1' as objects_1, 'value2' as objects_2 from TEST_DB_OBJECT ";
	@Test
	public void testMapInnerObjectWithStaticMapperNoAsm() throws Exception {
		DbHelper.testQuery(new TestDbListObject(false), QUERY);
	}
	
	@Test
	public void testMapInnerObjectWithStaticMapperAsm() throws Exception {
		DbHelper.testQuery(new TestDbListObject(true), QUERY);
	}

	@Test
	public void testMapInnerFinalObjectWithStaticMapperNoAsm() throws Exception {
		DbHelper.testQuery(new TestDbFinalListObject(false), QUERY);
	}
	
	@Test
	public void testMapInnerFinalObjectWithStaticMapperAsm() throws Exception {
		DbHelper.testQuery(new TestDbFinalListObject(true), QUERY);
	}	
	
	@Test
	public void testMapDbListOfStringNoAsm() throws Exception {
		DbHelper.testQuery(new TestDbListString(false), QUERY_STRING_LIST);
	}
	
	@Test
	public void testMapListOfStringAsm() throws Exception {
		DbHelper.testQuery(new TestDbListString(true), QUERY_STRING_LIST);
	}	
	
	@Test
	public void testMapTestListObjectNoAsm() throws Exception {
		DbHelper.testQuery(new TestListObject(false), QUERY_LIST);
	}
	
	@Test
	public void testMapTestListObjectAsm() throws Exception {
		DbHelper.testQuery(new TestListObject(true), QUERY_LIST);
	}
}
