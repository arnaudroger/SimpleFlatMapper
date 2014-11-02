package org.sfm.jdbc.jooq;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.jdbc.DbHelper;
import org.sfm.jooq.SfmRecordMapperProvider;

public class JooqMapperTest {

	@Test
	public void testMapper() throws Exception {
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
}
