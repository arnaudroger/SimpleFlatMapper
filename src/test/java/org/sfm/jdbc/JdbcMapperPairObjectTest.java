package org.sfm.jdbc;

import org.junit.Test;
import org.sfm.beans.Bar;
import org.sfm.beans.Foo;
import org.sfm.beans.Pair;
import org.sfm.reflect.TypeReference;
import org.sfm.utils.RowHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JdbcMapperPairObjectTest {

	private static final String QUERY = "select "
			+ "'first_bar' as first_bar, "
			+ "'second_bar' as second_bar, "
			+ "'second_foo' as second_foo "
			+ "from TEST_DB_OBJECT where id = 1 ";

	@Test
	public void testMapGenericObjectWithStaticMapper() throws Exception {
		JdbcMapperBuilder<Pair<Bar, Foo>> builder = JdbcMapperFactoryHelper.asm().newBuilder(new TypeReference<Pair<Bar, Foo>>() {
        })
                .addMapping("first_bar")
                .addMapping("second_bar")
                .addMapping("second_foo");

		final JdbcMapper<Pair<Bar, Foo>> mapper = builder.mapper();

		DbHelper.testQuery(new RowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement t) throws Exception {
				ResultSet rs = t.executeQuery();
				rs.next();
				Pair<Bar, Foo> object = mapper.map(rs);
				assertNotNull(object);
				assertNotNull(object.getFirst());
				assertEquals("first_bar", object.getFirst().getBar());
				assertNotNull(object.getSecond());
				assertEquals("second_bar", object.getSecond().getBar());
				assertEquals("second_foo", object.getSecond().getFoo());
			}
		}, QUERY);
	}
}
