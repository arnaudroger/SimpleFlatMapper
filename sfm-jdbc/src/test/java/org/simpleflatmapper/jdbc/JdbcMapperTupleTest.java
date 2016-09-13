package org.simpleflatmapper.jdbc;

import org.junit.Test;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.tuple.Tuple3;
import org.simpleflatmapper.tuple.Tuple4;
import org.simpleflatmapper.tuple.Tuple5;
import org.simpleflatmapper.tuple.Tuples;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.test.jdbc.TestRowHandler;
import org.simpleflatmapper.util.ListCollector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JdbcMapperTupleTest {
	
	@Test
	public void testTuple2OnString() throws Exception {
		JdbcMapperBuilder<Tuple2<String, String>> builder = JdbcMapperFactoryHelper.asm().newBuilder(Tuples.typeDef(String.class, String.class));

		builder.addMapping("element0");
		builder.addMapping("element1");

		final JdbcMapper<Tuple2<String, String>> mapper = builder.mapper();

		DbHelper.testQuery(
				new TestRowHandler<PreparedStatement>() {
					@Override
					public void handle(PreparedStatement preparedStatement) throws Exception {
						ResultSet rs = preparedStatement.executeQuery();
						try {
							List<Tuple2<String, String>> list = mapper.forEach(rs, new ListCollector<Tuple2<String, String>>()).getList();

							assertEquals(1, list.size());

							Tuple2<String, String> tuple2 = list.get(0);
							assertEquals("1", tuple2.getElement0());
							assertEquals("2", tuple2.getElement1());
						} finally {
							try {
								rs.close();
							} catch (Exception e) {
							}
						}
					}
				},
				"select '1', '2' from  TEST_DB_OBJECT where id = 1"

		);
	}

	@Test
	public void testTuple3OnString() throws Exception {
		JdbcMapperBuilder<Tuple3<String, String, Long>> builder = JdbcMapperFactoryHelper.asm().newBuilder(Tuples.typeDef(String.class, String.class, Long.class));

		builder.addMapping("element0");
		builder.addMapping("element1");
		builder.addMapping("element2");

		final JdbcMapper<Tuple3<String, String, Long>> mapper = builder.mapper();

		DbHelper.testQuery(
				new TestRowHandler<PreparedStatement>() {
					@Override
					public void handle(PreparedStatement preparedStatement) throws Exception {
						ResultSet rs = preparedStatement.executeQuery();
						try  {
							List<Tuple3<String, String, Long>> list = mapper.forEach(rs, new ListCollector<Tuple3<String, String, Long>>()).getList();

							assertEquals(1, list.size());

							Tuple3<String, String, Long> tuple = list.get(0);
							assertEquals("1", tuple.getElement0());
							assertEquals("2", tuple.getElement1());
							assertEquals(3l, tuple.getElement2().longValue());
						} finally {
							try { rs.close(); } catch (Exception e) {}
						}
					}
				},
				"select '1', '2', 3 from  TEST_DB_OBJECT where id = 1"

		);
	}

	@Test
	public void testTuple4OnString() throws Exception {
		JdbcMapperBuilder<Tuple4<String, String, Long, Integer>> builder = JdbcMapperFactoryHelper.asm().newBuilder(Tuples.typeDef(String.class, String.class, Long.class, Integer.class));

		builder.addMapping("element0");
		builder.addMapping("element1");
		builder.addMapping("element2");
		builder.addMapping("element3");

		final JdbcMapper<Tuple4<String, String, Long, Integer>> mapper = builder.mapper();

		DbHelper.testQuery(
				new TestRowHandler<PreparedStatement>() {
					@Override
					public void handle(PreparedStatement preparedStatement) throws Exception {
						ResultSet rs = preparedStatement.executeQuery();
						try  {
							List<Tuple4<String, String, Long, Integer>> list = mapper.forEach(rs, new ListCollector<Tuple4<String, String, Long, Integer>>()).getList();

							assertEquals(1, list.size());

							Tuple4<String, String, Long, Integer> tuple = list.get(0);
							assertEquals("1", tuple.getElement0());
							assertEquals("2", tuple.getElement1());
							assertEquals(3l, tuple.getElement2().longValue());
							assertEquals(4, tuple.getElement3().intValue());
						} finally {
							try { rs.close(); } catch (Exception e) {}
						}
					}
				},
				"select '1', '2', 3, 4 from  TEST_DB_OBJECT where id = 1"

		);
	}

	@Test
	public void testTuple5OnString() throws Exception {
		JdbcMapperBuilder<Tuple5<String, String, Long, Integer, Float>> builder = JdbcMapperFactoryHelper.asm().newBuilder(Tuples.typeDef(String.class, String.class, Long.class, Integer.class, Float.class));

		builder.addMapping("element0");
		builder.addMapping("element1");
		builder.addMapping("element2");
		builder.addMapping("element3");
		builder.addMapping("element4");

		final JdbcMapper<Tuple5<String, String, Long, Integer, Float>> mapper = builder.mapper();

		DbHelper.testQuery(
				new TestRowHandler<PreparedStatement>() {
					@Override
					public void handle(PreparedStatement preparedStatement) throws Exception {
						ResultSet rs = preparedStatement.executeQuery();
						try  {
							List<Tuple5<String, String, Long, Integer, Float>> list = mapper.forEach(rs, new ListCollector<Tuple5<String, String, Long, Integer, Float>>()).getList();

							assertEquals(1, list.size());

							Tuple5<String, String, Long, Integer, Float> tuple = list.get(0);
							assertEquals("1", tuple.first());
							assertEquals("2", tuple.second());
							assertEquals(3l, tuple.third().longValue());
							assertEquals(4, tuple.fourth().intValue());
							assertEquals(3.3, tuple.fifth().floatValue(), 0.0001);
						} finally {
							try { rs.close(); } catch (Exception e) {}
						}
					}
				},
				"select '1', '2', 3, 4, 3.3 from  TEST_DB_OBJECT where id = 1"

		);
	}
}