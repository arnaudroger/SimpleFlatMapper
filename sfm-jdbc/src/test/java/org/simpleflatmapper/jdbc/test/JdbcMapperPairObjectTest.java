package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperBuilder;
import org.simpleflatmapper.test.beans.Bar;
import org.simpleflatmapper.test.beans.Foo;
import org.simpleflatmapper.test.beans.Pair;
import org.simpleflatmapper.util.TypeReference;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.test.jdbc.TestRowHandler;

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

		DbHelper.testQuery(new TestRowHandler<PreparedStatement>() {
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
