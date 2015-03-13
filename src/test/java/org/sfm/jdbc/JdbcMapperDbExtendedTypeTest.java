package org.sfm.jdbc;

import org.junit.Test;
import org.sfm.beans.DbExtendedType;
import org.sfm.utils.ListHandler;
import org.sfm.utils.RowHandler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Time;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class JdbcMapperDbExtendedTypeTest {
	
	@Test
	public void testMapExtendedType() throws Exception {
		
		final JdbcMapper<DbExtendedType> mapper = JdbcMapperFactoryHelper.asm().newMapper(DbExtendedType.class);
		
		
		DbHelper.testQuery(new RowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement t) throws Exception {
				List<DbExtendedType> list = mapper.forEach(t.executeQuery(), new ListHandler<DbExtendedType>()).getList();
				assertEquals(1, list.size());
				DbExtendedType o = list.get(0);
				assertDbExtended(o);

				
			}


		}, "select * from db_extended_type");
	}
	@SuppressWarnings("deprecation")
	public static void assertDbExtended(DbExtendedType o) {
		assertArrayEquals(new byte[] { 'a', 'b', 'c' }, o.getBytes());
		assertEquals(new BigInteger("123"), o.getBigInteger());
		assertEquals(new BigDecimal("123.321").toString(), o.getBigDecimal().toString());
		assertEquals(new Time(7, 8, 9), o.getTime());
		assertEquals(new Date(114, 10, 2), o.getDate());
		assertArrayEquals(new String[] { "HOT", "COLD"}, o.getStringArray());
		assertEquals(Arrays.asList( "COLD", "FREEZING"), o.getStringList());
	}

}