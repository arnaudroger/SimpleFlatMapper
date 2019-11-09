package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.DynamicJdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperBuilder;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.test.beans.DbArrayObject;
import org.simpleflatmapper.test.beans.DbArrayOfString;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.test.jdbc.TestRowHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class JdbcMapperArrayTest {
	
	private static final class TestDbArrayObject implements
			TestRowHandler<PreparedStatement> {
		
		private final boolean asm ;
		
		public TestDbArrayObject(boolean asm) {
			this.asm = asm;
		}

		@Override
		public void handle(PreparedStatement t) throws Exception {
			ResultSet rs = t.executeQuery();
			final JdbcMapper<DbArrayObject> mapper =

                    JdbcMapperFactoryHelper.asm().useAsm(asm).newBuilder(DbArrayObject.class)
						.addMapping(rs.getMetaData())
						.mapper();
			
			rs.next();
			
			DbArrayObject object = mapper.map(rs);
			
			assertEquals(12, object.getId());
			assertEquals(3, object.getObjects().length);
			assertNull(object.getObjects()[0]);
			DbHelper.assertDbObjectMapping(object.getObjects()[1]);
			DbHelper.assertDbObjectMapping(object.getObjects()[2]);
		}
	}


	private static final class TestArrayObject implements
			TestRowHandler<PreparedStatement> {
		
		private final boolean asm ;
		public TestArrayObject(boolean asm) {
			this.asm = asm;
		}

		@Override
		public void handle(PreparedStatement t) throws Exception {
			ResultSet rs = t.executeQuery();
			final JdbcMapper<DbObject[]> mapper =
                    JdbcMapperFactoryHelper.asm().useAsm(asm).newBuilder(DbObject[].class)
						.addMapping(rs.getMetaData())
						.mapper();
			
			rs.next();
			
			DbObject[] list = mapper.map(rs);
			
			assertEquals(3, list.length);
			assertNull(list[0]);
			DbHelper.assertDbObjectMapping(list[1]);
			DbHelper.assertDbObjectMapping(list[2]);
		}
	}
	
	private static final class TestDbArrayString implements
			TestRowHandler<PreparedStatement> {

		private final boolean asm;

		public TestDbArrayString(boolean asm) {
			this.asm = asm;
		}

		@Override
		public void handle(PreparedStatement t) throws Exception {
			ResultSet rs = t.executeQuery();
			
			JdbcMapperBuilder<DbArrayOfString> builder = JdbcMapperFactoryHelper.asm().useAsm(asm).newBuilder(DbArrayOfString.class).addMapping(rs.getMetaData());
			
			
			final JdbcMapper<DbArrayOfString> mapper = 
				 builder.mapper();

			rs.next();

			DbArrayOfString object = mapper.map(rs);

			assertEquals(12, object.getId());
			assertEquals(3, object.getObjects().length);
			assertNull(object.getObjects()[0]);
			assertEquals("value1", object.getObjects()[1]);
			assertEquals("value2", object.getObjects()[2]);
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
		DbHelper.testQuery(new TestDbArrayObject(false), QUERY);
	}
	
	@Test
	public void testMapInnerObjectWithStaticMapperAsm() throws Exception {
		DbHelper.testQuery(new TestDbArrayObject(true), QUERY);
	}

	
	@Test
	public void testMapDbArrayOfStringNoAsm() throws Exception {
		DbHelper.testQuery(new TestDbArrayString(false), QUERY_STRING_LIST);
	}
	
	@Test
	public void testMapArrayOfStringAsm() throws Exception {
		DbHelper.testQuery(new TestDbArrayString(true), QUERY_STRING_LIST);
	}	
	
	@Test
	public void testMapTestArrayObjectNoAsm() throws Exception {
		DbHelper.testQuery(new TestArrayObject(false), QUERY_LIST);
	}
	
	@Test
	public void testMapTestArrayObjectAsm() throws Exception {
		DbHelper.testQuery(new TestArrayObject(true), QUERY_LIST);
	}

	@Test
	public void testPlurals() throws Exception {
		DynamicJdbcMapper<Plural> mapper = JdbcMapperFactory.newInstance().addKeys("id").newMapper(Plural.class);
		Connection dbConnection = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);

		if (dbConnection == null) return;

		try {
			Statement statement = dbConnection.createStatement();
			try {
				ResultSet rs = statement.executeQuery("WITH vals (id, label) AS (VALUES (1,'l1'), (1, 'l2'), (2, 'l3')) SELECT * FROM vals");

				Iterator<Plural> iterator = mapper.iterator(rs);

				assertTrue(iterator.hasNext());
				assertEquals(new Plural(1, Arrays.asList("l1", "l2")), iterator.next());

				assertTrue(iterator.hasNext());
				assertEquals(new Plural(2, Arrays.asList("l3")), iterator.next());

				assertFalse(iterator.hasNext());

			} finally {
				statement.close();
			}

		} finally {

			dbConnection.close();
		}
	}

	public static class Plural {
		public final long id;
		public final List<String> labels;

		public Plural(long id, List<String> labels) {
			this.id = id;
			this.labels = labels;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Plural plural = (Plural) o;

			if (id != plural.id) return false;
			return labels != null ? labels.equals(plural.labels) : plural.labels == null;
		}

		@Override
		public int hashCode() {
			int result = (int) (id ^ (id >>> 32));
			result = 31 * result + (labels != null ? labels.hashCode() : 0);
			return result;
		}

		@Override
		public String toString() {
			return "Plural{" +
					"id=" + id +
					", labels=" + labels +
					'}';
		}
	}
}
