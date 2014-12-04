package org.sfm.jdbc;

import org.junit.Test;
import org.sfm.beans.DbFinalObject;
import org.sfm.beans.DbObject;
import org.sfm.reflect.ReflectionService;
import org.sfm.tuples.Tuple2;
import org.sfm.utils.ListHandler;
import org.sfm.utils.RowHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class JdbcMapperTupleTest {
	
	@Test
	public void testTuple2OnString() throws Exception {
		JdbcMapperBuilder<Tuple2<String, String>> builder = new JdbcMapperBuilder<Tuple2<String, String>>(Tuple2.typeDef(String.class, String.class));
		
		addColumn(builder);
		
		final JdbcMapper<Tuple2<String, String>> mapper = builder.mapper();

		DbHelper.testQuery(
				new RowHandler<PreparedStatement>() {
					@Override
					public void handle(PreparedStatement preparedStatement) throws Exception {
						ResultSet rs = preparedStatement.executeQuery();
						try  {
							List<Tuple2<String, String>> list = mapper.forEach(rs, new ListHandler<Tuple2<String, String>>()).getList();

							assertEquals(1, list.size());

							Tuple2<String, String> tuple2 = list.get(0);
							assertEquals("1", tuple2.getElement1());
							assertEquals("2", tuple2.getElement2());
						} finally {
							try { rs.close(); } catch (Exception e) {}
						}
					}
				},
				"select '1', '2' from  TEST_DB_OBJECT where id = 1"

		);
	}

	public static <T> JdbcMapperBuilder<T> addColumn(JdbcMapperBuilder<T> builder) {
		builder.addMapping("element1");
		builder.addMapping("element2");
		return builder;
	}

}