package org.simpleflatmapper.jooq.test;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordType;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.junit.Test;
import org.simpleflatmapper.jooq.JooqRecordMapperWrapper;
import org.simpleflatmapper.jooq.SfmRecordMapperProvider;
import org.simpleflatmapper.jooq.SfmRecordMapperProviderFactory;
import org.simpleflatmapper.test.beans.DbExtendedType;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.jdbc.DbHelper;

import java.sql.Connection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JooqMapperTest {

	@Test
	@SuppressWarnings("unchecked")
	public void testCacheMapper() {
		
		SfmRecordMapperProvider recordMapperProvider = SfmRecordMapperProviderFactory.newInstance().newProvider();;
		RecordType rt = mock(RecordType.class);
		Field field1 = mock(Field.class);
		when(field1.getName()).thenReturn("id");
		when(field1.getType()).thenReturn(long.class);
		when(rt.size()).thenReturn(1);
		when(rt.fields()).thenReturn(new Field[] {field1});

		JooqRecordMapperWrapper provider1 =
				(JooqRecordMapperWrapper) recordMapperProvider.<Record, DbObject>provide(rt, DbObject.class);
		JooqRecordMapperWrapper provider2 =
				(JooqRecordMapperWrapper) recordMapperProvider.<Record, DbObject>provide(rt, DbObject.class);
		assertSame(provider1.getMapper(), provider2.getMapper());
	}

	@Test
	public void testIgnoreFields() throws Exception {
		Connection conn = DbHelper.objectDb();

		DSLContext dsl = DSL
				.using(new DefaultConfiguration().set(conn)
						.set(SQLDialect.HSQLDB)
						.set(SfmRecordMapperProviderFactory.newInstance().addAlias("id", "noId").ignorePropertyNotFound().newProvider()));
		
		List<DbObject> list = dsl.select()
				.from("TEST_DB_OBJECT").fetchInto(DbObject.class);
		
		assertEquals(2, list.size());

		assertEquals(0, list.get(0).getId());
		list.get(0).setId(1);
		DbHelper.assertDbObjectMapping(list.get(0));
	}

	@Test
	public void testMapperDbObject() throws Exception {
		Connection conn = DbHelper.objectDb();

		DSLContext dsl = DSL
				.using(new DefaultConfiguration().set(conn)
						.set(SQLDialect.HSQLDB)
						.set(new SfmRecordMapperProvider()));

		List<DbObject> list = dsl.select()
				.from("TEST_DB_OBJECT").fetchInto(DbObject.class);

		assertEquals(2, list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
	}
	
	@Test
	public void testMapperDbExtendedType() throws Exception {
		Connection conn = DbHelper.objectDb();

		DSLContext dsl = DSL
				.using(new DefaultConfiguration().set(conn)
						.set(SQLDialect.HSQLDB)
						.set(SfmRecordMapperProviderFactory.newInstance().newProvider()));
		
		List<DbExtendedType> list = dsl.select()
				.from("db_extended_type").fetchInto(DbExtendedType.class);
		
		
		assertEquals(1, list.size());
		DbExtendedType o = list.get(0);
		
		DbExtendedType.assertDbExtended(o);
		
	}


}
