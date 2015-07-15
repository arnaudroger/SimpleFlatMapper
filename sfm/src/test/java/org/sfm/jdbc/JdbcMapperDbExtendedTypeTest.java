package org.sfm.jdbc;

import org.junit.Test;
import org.sfm.beans.DbExtendedType;
import org.sfm.utils.ListHandler;

import java.sql.PreparedStatement;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.sfm.beans.DbExtendedType.assertDbExtended;

public class JdbcMapperDbExtendedTypeTest {
	
	@Test
	public void testMapExtendedType() throws Exception {
		
		final JdbcMapper<DbExtendedType> mapper = JdbcMapperFactoryHelper.asm().newMapper(DbExtendedType.class);
		
		
		DbHelper.testQuery(new TestRowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement t) throws Exception {
				List<DbExtendedType> list = mapper.forEach(t.executeQuery(), new ListHandler<DbExtendedType>()).getList();
				assertEquals(1, list.size());
				DbExtendedType o = list.get(0);
				assertDbExtended(o);

				
			}


		}, "select * from db_extended_type");
	}


}