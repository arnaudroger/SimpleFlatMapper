package org.simpleflatmapper.jdbc.test;

import org.hsqldb.result.ResultMetaData;
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

import java.sql.*;
import java.util.Arrays;
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
                        try {
                            List<Tuple3<String, String, Long>> list = mapper.forEach(rs, new ListCollector<Tuple3<String, String, Long>>()).getList();

                            assertEquals(1, list.size());

                            Tuple3<String, String, Long> tuple = list.get(0);
                            assertEquals("1", tuple.getElement0());
                            assertEquals("2", tuple.getElement1());
                            assertEquals(3l, tuple.getElement2().longValue());
                        } finally {
                            try {
                                rs.close();
                            } catch (Exception e) {
                            }
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
                        try {
                            List<Tuple4<String, String, Long, Integer>> list = mapper.forEach(rs, new ListCollector<Tuple4<String, String, Long, Integer>>()).getList();

                            assertEquals(1, list.size());

                            Tuple4<String, String, Long, Integer> tuple = list.get(0);
                            assertEquals("1", tuple.getElement0());
                            assertEquals("2", tuple.getElement1());
                            assertEquals(3l, tuple.getElement2().longValue());
                            assertEquals(4, tuple.getElement3().intValue());
                        } finally {
                            try {
                                rs.close();
                            } catch (Exception e) {
                            }
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
                        try {
                            List<Tuple5<String, String, Long, Integer, Float>> list = mapper.forEach(rs, new ListCollector<Tuple5<String, String, Long, Integer, Float>>()).getList();

                            assertEquals(1, list.size());

                            Tuple5<String, String, Long, Integer, Float> tuple = list.get(0);
                            assertEquals("1", tuple.first());
                            assertEquals("2", tuple.second());
                            assertEquals(3l, tuple.third().longValue());
                            assertEquals(4, tuple.fourth().intValue());
                            assertEquals(3.3, tuple.fifth().floatValue(), 0.0001);
                        } finally {
                            try {
                                rs.close();
                            } catch (Exception e) {
                            }
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
                        .reflectionService(ReflectionService.newInstance())
                        .addKeys("id", "resource_id")
                        .newBuilder(new TypeReference<Tuple2<Integer, List<Privilege>>>() {
                        })
                        .addMapping("id", 1, 4)
                        .addMapping("name", 2, 12)
                        .addMapping("resource_id", 3, 4)
                        .mapper();

        Connection dbConnection = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);

        if (dbConnection == null) {
            return;
        }

        Statement statement = dbConnection.createStatement();

		String sql = "with \n" +
				"\tresource_privileges(resource_id, privilege_id) AS (VALUES(1, 10), (2, 10), (2, 11)),\n" +
				"\tprivilege (id, name) as (values(10, 'write'), (11, 'read'))\n" +
				"select privilege.id as id, privilege.name as name, resource_privileges.resource_id as resource_id \n" +
				"from privilege join resource_privileges on resource_privileges.privilege_id = privilege.id\n" +
				"order by resource_privileges.resource_id";


		/*

id = 10
name = write
resource_id = 1

id = 10
name = write
resource_id = 2

id = 11
name = read
resource_id = 3

		 */

		ResultSet rs = statement.executeQuery(sql);
		print(rs);

		 rs = statement.executeQuery(sql);
        List<Tuple2<Integer, List<Privilege>>> list = mapper.forEach(rs, new ListCollector<Tuple2<Integer, List<Privilege>>>()).getList();

		Privilege read = new Privilege(11, "read");
		Privilege write = new Privilege(10, "write");


		assertEquals(2, list.size());

        assertEquals((Integer) 1, list.get(0).first());
		assertEquals(Arrays.asList(write), list.get(0).second());

        assertEquals((Integer) 2, list.get(1).first());
		assertEquals(Arrays.asList(write, read), list.get(1).second());

        dbConnection.close();

    }

	private void print(ResultSet rs) throws SQLException {
		ResultSetMetaData md = rs.getMetaData();

		while(rs.next()) {
			for(int i = 1;i <= md.getColumnCount(); i++) {
				System.out.println(md.getColumnLabel(i) + " = " + rs.getString(i));
			}
		}
	}

	//new TypeReference<Tuple2<Integer, List<Privilege>>>() {}
    //privilege.id, privilege.name, resource_privileges.resource_id
    public static class Privilege {
        public int id;
        public String name;

		public Privilege(int id, String name) {
			this.id = id;
			this.name = name;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Privilege privilege = (Privilege) o;

			if (id != privilege.id) return false;
			return name != null ? name.equals(privilege.name) : privilege.name == null;
		}

		@Override
		public int hashCode() {
			int result = id;
			result = 31 * result + (name != null ? name.hashCode() : 0);
			return result;
		}
	}
}
