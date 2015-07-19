package org.sfm.jooq;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.junit.Test;
import org.sfm.beans.DbExtendedType;
import org.sfm.beans.DbObject;
import org.sfm.test.jdbc.DbHelper;

import java.sql.Connection;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JooqMapperTest {

	@Test
	public void testMapperDbObject() throws Exception {
		Connection conn = DbHelper.objectDb();

		DSLContext dsl = DSL
				.using(new DefaultConfiguration().set(conn)
						.set(SQLDialect.HSQLDB)
						.set(new SfmRecordMapperProvider()));
		
		List<DbObject> list = dsl.select()
				.from("TEST_DB_OBJECT").fetchInto(DbObject.class);
		
		assertEquals(1, list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
	}
	
	@Test
	public void testMapperDbExtendedType() throws Exception {
		Connection conn = DbHelper.objectDb();

		DSLContext dsl = DSL
				.using(new DefaultConfiguration().set(conn)
						.set(SQLDialect.HSQLDB)
						.set(new SfmRecordMapperProvider()));
		
		List<DbExtendedType> list = dsl.select()
				.from("db_extended_type").fetchInto(DbExtendedType.class);
		
		
		assertEquals(1, list.size());
		DbExtendedType o = list.get(0);
		
		DbExtendedType.assertDbExtended(o);
		
	}
}
