package org.sfm.jdbc;

import static org.junit.Assert.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.Test;
import org.sfm.beans.DbFinalListObject;
import org.sfm.beans.DbListObject;
import org.sfm.reflect.ReflectionService;
import org.sfm.utils.Handler;

public class JdbcMapperListTest {
	
	private static final class TestDbFinalListObject implements
			Handler<PreparedStatement> {

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
			Handler<PreparedStatement> {
		
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

	
	
	private static final String QUERY = "select 12 as id, "
			+ " 1 as objects_1_id, 'name 1' as objects_1_name, 'name1@mail.com' as objects_1_email, TIMESTAMP'2014-03-04 11:10:03' as objects_1_creation_time, 2 as objects_1_type_ordinal, 'type4' as objects_1_type_name, "
			+ " 1 as objects_2_id, 'name 1' as objects_2_name, 'name1@mail.com' as objects_2_email, TIMESTAMP'2014-03-04 11:10:03' as objects_2_creation_time, 2 as objects_2_type_ordinal, 'type4' as objects_2_type_name "
			+ " from TEST_DB_OBJECT ";

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
}
