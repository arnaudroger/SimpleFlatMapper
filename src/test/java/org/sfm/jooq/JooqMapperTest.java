package org.sfm.jooq;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.junit.Test;
import org.sfm.beans.DbExtentedType;
import org.sfm.beans.DbObject;
import org.sfm.jdbc.DbHelper;
import org.sfm.jdbc.JdbcMapperDbExtentedTypeTest;

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
	public void testMapperDbExtentedType() throws Exception {
		Connection conn = DbHelper.objectDb();

		DSLContext dsl = DSL
				.using(new DefaultConfiguration().set(conn)
						.set(SQLDialect.HSQLDB)
						.set(new SfmRecordMapperProvider()));
		
		List<DbExtentedType> list = dsl.select()
				.from("db_extented_type").fetchInto(DbExtentedType.class);
		
		
		assertEquals(1, list.size());
		DbExtentedType o = list.get(0);
		
		JdbcMapperDbExtentedTypeTest.assertDbExtended(o);
		
	}
}
