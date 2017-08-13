package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperBuilder;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.test.jdbc.TestRowHandler;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.tuple.Tuple3;
import org.simpleflatmapper.tuple.Tuple4;
import org.simpleflatmapper.tuple.Tuple5;
import org.simpleflatmapper.tuple.Tuples;
import org.simpleflatmapper.util.ListCollector;
import org.simpleflatmapper.util.TypeReference;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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

	@Test
	public void testIssue450() throws Exception {
		JdbcMapper<Tuple2<Integer, List<Privilege>>> mapper = 
				JdbcMapperFactory
						.newInstance()
						.reflectionService(ReflectionService.newInstance().withSelfScoreFullName(true))
						.addKeys("resource_id", "id")
						.newBuilder(new TypeReference<Tuple2<Integer, List<Privilege>>>() {})
						.addMapping("id", 1, 4)
						.addMapping("name", 2, 12)
						.addMapping("resource_id", 3, 4)
						.mapper();

		Connection dbConnection = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
		
		if (dbConnection == null) {
			return;
		}

		Statement statement = dbConnection.createStatement();
		
		ResultSet rs = statement.executeQuery(
				"with \n" +
						"\tresource_privileges(resource_id, privilege_id) AS (VALUES(1, 10), (2, 10), (3, 11)),\n" +
						"\tprivilege (id, name) as (values(10, 'write'), ('11', 'read'))\n" +
						"select privilege.id, privilege.name, resource_privileges.resource_id\n" +
						"from privilege join resource_privileges on resource_privileges.privilege_id = privilege.id\n" +
						"order by resource_privileges.resource_id");
		List<Tuple2<Integer, List<Privilege>>> list = mapper.forEach(rs, new ListCollector<Tuple2<Integer, List<Privilege>>>()).getList();
		
		
		assertEquals(2, list.size());

		assertEquals((Integer)10, list.get(0).first());
		assertEquals((Integer)11, list.get(1).first());
		
		assertEquals(2, list.get(0).second().size());
		assertEquals(1, list.get(1).second().size());
		
		dbConnection.close();

	}
	
	public static class Privilege {
		public int id;
		public String name;
	}
}
