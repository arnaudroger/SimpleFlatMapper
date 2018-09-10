package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperBuilder;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.test.beans.DbFinalObject;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.map.property.DefaultValueProperty;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.test.jdbc.TestRowHandler;
import org.simpleflatmapper.util.ListCollector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
//IFJAVA8_START
import java.util.function.Consumer;
//IFJAVA8_END
import static org.junit.Assert.*;

public class JdbcMapperDbObjectTest {

	@Test
	public void testDefaultValue() throws Exception {
		final JdbcMapper<DbObject> mapper = JdbcMapperFactory
				.newInstance()
				.addColumnProperty("name", new DefaultValueProperty<String>("defaultName")).newMapper(DbObject.class);

		DbHelper.testQuery(new TestRowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement preparedStatement) throws Exception {
				ResultSet rs = preparedStatement.executeQuery();

				rs.next();

				DbObject object = mapper.map(rs);

				assertEquals(1, object.getId());
				assertEquals("namers", object.getName());


			}
		}, "SELECT id, 'namers' as name from TEST_DB_OBJECT where id = 1");

		DbHelper.testQuery(new TestRowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement preparedStatement) throws Exception {
				ResultSet rs = preparedStatement.executeQuery();

				rs.next();

				DbObject object = mapper.map(rs);

				assertEquals(1, object.getId());
				assertEquals("defaultName", object.getName());


			}
		}, "SELECT id from TEST_DB_OBJECT where id = 1");


		DbHelper.testQuery(new TestRowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement preparedStatement) throws Exception {
				ResultSet rs = preparedStatement.executeQuery();

				rs.next();

				DbObject object = mapper.map(rs);

				assertEquals(1, object.getId());
				assertEquals("defaultName", object.getName());


			}
		}, "SELECT id, null as name from TEST_DB_OBJECT where id = 1");
	}

	@Test
	public void testColumn() throws Exception {
		JdbcMapperBuilder<DbObject> builder = JdbcMapperFactoryHelper.asm().newBuilder(DbObject.class);

		addColumn(builder);

		final JdbcMapper<DbObject> mapper = builder.mapper();

		testDbObjectMapper(mapper);
	}

	public static <T> JdbcMapperBuilder<T> addColumn(JdbcMapperBuilder<T> builder) {
		builder.addMapping("id");
		builder.addMapping("name");
		builder.addMapping("email");
		builder.addMapping("creation_time");
		builder.addMapping("type_ordinal");
		builder.addMapping("type_name");
		return builder;
	}

	@Test
	public void testColumnFinalProperty() throws Exception {
		JdbcMapperBuilder<DbFinalObject> builder = JdbcMapperFactoryHelper.asm().newBuilder(DbFinalObject.class);

		addColumn(builder);

		final JdbcMapper<DbFinalObject> mapper = builder.mapper();

		DbHelper.testDbObjectFromDb(new TestRowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				List<DbFinalObject> objects = mapper.forEach(ps.executeQuery(), new ListCollector<DbFinalObject>()).getList();
				assertEquals(1, objects.size());
				DbHelper.assertDbObjectMapping(objects.get(0));
			}
		});
	}
	private void testDbObjectMapper(final JdbcMapper<DbObject> mapper)
			throws Exception {
		DbHelper.testDbObjectFromDb(new TestRowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				List<DbObject> objects = mapper.forEach(ps.executeQuery(), new ListCollector<DbObject>()).getList();
				assertEquals(1, objects.size());
				DbHelper.assertDbObjectMapping(objects.get(0));
			}
		});
	}

	@Test
	public void testDbObjectMapperWithIterator()
			throws Exception {
		JdbcMapperBuilder<DbObject> builder = JdbcMapperFactoryHelper.asm().newBuilder(DbObject.class);

		testMapperBuilderWithIterator(builder);
	}
	@Test
	public void testDbObjectMapperWithIteratorNoAsm()
			throws Exception {
		JdbcMapperBuilder<DbObject> builder = JdbcMapperFactoryHelper.noAsm().newBuilder(DbObject.class);

		testMapperBuilderWithIterator(builder);
	}
	private void testMapperBuilderWithIterator(JdbcMapperBuilder<DbObject> builder) throws Exception {
		addColumn(builder);

		final JdbcMapper<DbObject> mapper = builder.mapper();

		DbHelper.testDbObjectFromDb(new TestRowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				Iterator<DbObject> objects = mapper.iterator(ps.executeQuery());
				assertTrue(objects.hasNext());
				DbHelper.assertDbObjectMapping(objects.next());
				assertFalse(objects.hasNext());
			}
		});
	}

	//IFJAVA8_START
	@Test
	public void testDbObjectMapperWithStream()
			throws Exception {

		JdbcMapperBuilder<DbObject> builder = JdbcMapperFactoryHelper.asm().newBuilder(DbObject.class);
		addColumn(builder);

		final JdbcMapper<DbObject> mapper = builder.mapper();

		DbHelper.testDbObjectFromDb(new TestRowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				mapper.stream(ps.executeQuery()).forEach(
						new Consumer<DbObject>() {
							@Override
							public void accept(DbObject o) {
								try {
									DbHelper.assertDbObjectMapping(o);
								} catch (Exception e) {
									throw new RuntimeException(e);
								}
							}
						}
				);
			}
		});
	}

	@Test
	public void testDbObjectMapperWithStreamNoAsm()
			throws Exception {
		JdbcMapperBuilder<DbObject> builder = JdbcMapperFactoryHelper.noAsm().newBuilder(DbObject.class);
		addColumn(builder);
		final JdbcMapper<DbObject> mapper = builder.mapper();

		DbHelper.testDbObjectFromDb(new TestRowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				mapper.stream(ps.executeQuery()).forEach(
						new Consumer<DbObject>() {
							@Override
							public void accept(DbObject o) {
								try {
									DbHelper.assertDbObjectMapping(o);
								} catch (Exception e) {
									throw new RuntimeException(e);
								}
							}
						}
				);
			}
		});
	}

	@Test
	public void testDbObjectMapperWithStreamTryAdvance()
			throws Exception {
		JdbcMapperBuilder<DbObject> builder = JdbcMapperFactoryHelper.asm().newBuilder(DbObject.class);
		addColumn(builder);
		final JdbcMapper<DbObject> mapper = builder.mapper();

		DbHelper.testDbObjectFromDb(new TestRowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				mapper.stream(ps.executeQuery()).limit(1).forEach(
						new Consumer<DbObject>() {
							@Override
							public void accept(DbObject o) {
								try {
									DbHelper.assertDbObjectMapping(o);
								} catch (Exception e) {
									throw new RuntimeException(e);
								}
							}
						}
				);
			}
		});
	}

	@Test
	public void testDbObjectMapperWithStreamTryAdvanceNoAsm()
			throws Exception {
		JdbcMapperBuilder<DbObject> builder = JdbcMapperFactoryHelper.noAsm().newBuilder(DbObject.class);
		addColumn(builder);
		final JdbcMapper<DbObject> mapper = builder.mapper();

		DbHelper.testDbObjectFromDb(new TestRowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				mapper.stream(ps.executeQuery()).limit(1).forEach(
						new Consumer<DbObject>() {
							@Override
							public void accept(DbObject o) {
								try {
									DbHelper.assertDbObjectMapping(o);
								} catch (Exception e) {
									throw new RuntimeException(e);
								}
							}
						}
				);
			}
		});
	}
	//IFJAVA8_END

}