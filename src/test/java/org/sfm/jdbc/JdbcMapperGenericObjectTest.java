package org.sfm.jdbc;

import org.junit.Test;
import org.sfm.beans.Db1GenericObject;
import org.sfm.utils.RowHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JdbcMapperGenericObjectTest {

	private static final String QUERY = "select 33 as id, "
			+ "'barbar' as bar_object_bar, "
			+ "'foobar' as foo_object_bar, "
			+ "'foofoo' as foo_object_foo "
			+ "from TEST_DB_OBJECT where id = 1 ";

	@Test
	public void testMapGenericObjectWithStaticMapper() throws Exception {
		JdbcMapperBuilder<Db1GenericObject> builder = new JdbcMapperBuilder<Db1GenericObject>(Db1GenericObject.class)
				.addMapping("id")
				.addMapping("bar_object_bar")
				.addMapping("foo_object_bar")
				.addMapping("foo_object_foo");

		final JdbcMapper<Db1GenericObject> mapper = builder.mapper();

		DbHelper.testQuery(new RowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement t) throws Exception {
				ResultSet rs = t.executeQuery();
				rs.next();
				Db1GenericObject object = mapper.map(rs);
				assertEquals(33, object.getId());
				assertNotNull(object.getBarObject());
				assertEquals("barbar", object.getBarObject().getBar());

				assertNotNull(object.getFooObject());
				assertEquals("foobar", object.getFooObject().getBar());
				assertEquals("foofoo", object.getFooObject().getFoo());
			}
		}, QUERY);
	}
}
