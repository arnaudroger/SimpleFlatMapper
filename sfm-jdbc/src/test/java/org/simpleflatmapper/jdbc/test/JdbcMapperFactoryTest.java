package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperBuilder;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.map.annotation.Key;
import org.simpleflatmapper.map.mapper.DefaultPropertyNameMatcherFactory;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.FieldMapperErrorHandler;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.ConsumerErrorHandler;
import org.simpleflatmapper.test.beans.DbFinalObject;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.beans.DbObjectWithAlias;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.test.beans.DbObjectWithEmptyAlias;
import org.simpleflatmapper.util.TypeReference;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.test.jdbc.TestRowHandler;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.tuple.Tuples;
import org.simpleflatmapper.util.ListCollector;
import org.simpleflatmapper.util.CheckedConsumer;

import java.lang.reflect.Type;
import java.sql.*;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class JdbcMapperFactoryTest {

	JdbcMapperFactory asmFactory = JdbcMapperFactoryHelper.asm();
	JdbcMapperFactory nonAsmFactory = JdbcMapperFactoryHelper.noAsm();


	@Test
	public void testFactoryOnTuples() {
		assertNotNull(asmFactory.newMapper(Tuples.typeDef(Date.class, Date.class)));
		assertNotNull(asmFactory.newBuilder(Tuples.typeDef(Date.class, Date.class)));
	}

    @Test
    public void testFactoryOnReferenceType() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        ResultSetMetaData metaData = mock(ResultSetMetaData.class);
        when(rs.getMetaData()).thenReturn(metaData);

        when(metaData.getColumnCount()).thenReturn(2);
        when(metaData.getColumnLabel(1)).thenReturn("e0");
        when(metaData.getColumnLabel(2)).thenReturn("e1");
        when(metaData.getColumnType(1)).thenReturn(Types.VARCHAR);
        when(metaData.getColumnType(2)).thenReturn(Types.VARCHAR);
        when(rs.next()).thenReturn(true, false);

        when(rs.getString(1)).thenReturn("v1");
        when(rs.getString(2)).thenReturn("v2");

        Tuple2<String, String> tuple2 = JdbcMapperFactoryHelper.asm().newMapper(new TypeReference<Tuple2<String, String>>() {
        }).iterator(rs).next();

        assertEquals("v1", tuple2.first());
        assertEquals("v2", tuple2.second());
    }

    @Test
    public void testFactoryOnReferenceTypeStatic() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true, false);

        when(rs.getString(1)).thenReturn("v1");
        when(rs.getString(2)).thenReturn("v2");

        Tuple2<String, String> tuple2 = JdbcMapperFactoryHelper.asm().newBuilder(new TypeReference<Tuple2<String, String>>() {
        }).addMapping("e0").addMapping("e1").mapper()
                .iterator(rs).next();

        assertEquals("v1", tuple2.first());
        assertEquals("v2", tuple2.second());
    }
    @Test
	public void testAsmDbObjectMappingFromDbWithMetaData()
			throws Exception {
		DbHelper.testDbObjectFromDb(new TestRowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				ResultSet rs = ps.executeQuery();
				JdbcMapper<DbObject> mapper = asmFactory.newMapper(DbObject.class, rs.getMetaData());
				assertMapPsDbObject(rs, mapper);
			}
		});
	}
	
	@Test
	public void testAsmDbObjectWithAliasMappingFromDbWithMetaData()
			throws Exception {
		DbHelper.testDbObjectFromDb(new TestRowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				ResultSet rs = ps.executeQuery();
				JdbcMapper<DbObjectWithAlias> mapper = asmFactory.newMapper(DbObjectWithAlias.class, rs.getMetaData());
				assertMapPsDbObjectWithAlias(rs, mapper);
			}
		});
	}

	@Test
	public void testAsmDbObjectWithEmptyAliasMappingFromDbWithMetaData()
			throws Exception {
		DbHelper.testDbObjectFromDb(new TestRowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				ResultSet rs = ps.executeQuery();
				JdbcMapper<DbObjectWithEmptyAlias> mapper = asmFactory.newMapper(DbObjectWithEmptyAlias.class, rs.getMetaData());
				assertMapPsDbObjectWithEmptyAlias(rs, mapper);
			}
		});
	}
	
	@Test
	public void testNonAsmDbObjectMappingFromDbWithMetaData()
			throws Exception {
		DbHelper.testDbObjectFromDb(new TestRowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				ResultSet rs = ps.executeQuery();
				JdbcMapper<DbObject> mapper = nonAsmFactory.newMapper(DbObject.class, rs.getMetaData());
				assertMapPsDbObject(rs, mapper);
            }
		});
	}
	
	@Test
	public void testAsmDbObjectMappingFromDbDynamic()
			throws SQLException, Exception, ParseException {
		DbHelper.testDbObjectFromDb(new TestRowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				JdbcMapper<DbObject> mapper = asmFactory.newMapper(DbObject.class);
				assertMapPsDbObject(ps.executeQuery(), mapper);
			}
		});
	}
	
	@Test
	public void testNonAsmDbObjectMappingFromDbDynamic()
			throws SQLException, Exception, ParseException {
		DbHelper.testDbObjectFromDb(new TestRowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				JdbcMapper<DbObject> mapper = nonAsmFactory.newMapper(DbObject.class);
				assertMapPsDbObject(ps.executeQuery(), mapper);
			}
		});
	}
	
	@Test
	public void testAsmFinalDbObjectMappingFromDbDynamic()
			throws SQLException, Exception, ParseException {
		DbHelper.testDbObjectFromDb(new TestRowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				JdbcMapper<DbFinalObject> mapper = asmFactory.newMapper(DbFinalObject.class);
				assertMapPsFinalDbObject(ps.executeQuery(), mapper);
			}
		});
	}
	
	@Test
	public void testNonAsmFinalDbObjectMappingFromDbDynamic()
			throws SQLException, Exception, ParseException {
		DbHelper.testDbObjectFromDb(new TestRowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				JdbcMapper<DbFinalObject> mapper = nonAsmFactory.newMapper(DbFinalObject.class);
				assertMapPsFinalDbObject(ps.executeQuery(), mapper);
			}
		});
	}	
	
	@Test
	public void testFieldErrorHandling()
			throws SQLException, Exception, ParseException {
		@SuppressWarnings("unchecked")
        FieldMapperErrorHandler<JdbcColumnKey> fieldMapperErrorHandler  = mock(FieldMapperErrorHandler.class);
		final Exception exception = new Exception("Error!");
		JdbcMapper<DbObject> mapper = JdbcMapperFactoryHelper.asm()
			.fieldMapperErrorHandler(fieldMapperErrorHandler)
			.addCustomFieldMapper("id",  new FieldMapper<ResultSet, DbObject>() {
				@Override
				public void mapTo(ResultSet source, DbObject target, MappingContext<? super ResultSet> mappingContext) throws Exception {
					throw exception;
				}
			}).newBuilder(DbObject.class).addMapping("id").mapper();
		
		List<DbObject> list = mapper.forEach(new MockDbObjectResultSet(1), new ListCollector<DbObject>()).getList();
		assertNotNull(list.get(0));
		verify(fieldMapperErrorHandler).errorMappingField(eq(new JdbcColumnKey("id", 1)), any(), same(list.get(0)), same(exception), any(Context.class));
	}
	
	
	@Test
	public void testFieldErrorHandlingOnResultSet()
			throws SQLException, Exception, ParseException {
		@SuppressWarnings("unchecked")
		FieldMapperErrorHandler<JdbcColumnKey> fieldMapperErrorHandler  = mock(FieldMapperErrorHandler.class);
		ResultSet rs = mock(ResultSet.class);
		
		final Exception exception = new SQLException("Error!");
		JdbcMapper<DbObject> mapper = JdbcMapperFactoryHelper.asm()
			.fieldMapperErrorHandler(fieldMapperErrorHandler)
			.newBuilder(DbObject.class).addMapping("id").mapper();
		
		when(rs.next()).thenReturn(true, false);
		when(rs.getLong(1)).thenThrow(exception);
		
		List<DbObject> list = mapper.forEach(rs, new ListCollector<DbObject>()).getList();
		assertNotNull(list.get(0));
		verify(fieldMapperErrorHandler).errorMappingField(eq(new JdbcColumnKey("id", 1)), any(), same(list.get(0)), same(exception), any(Context.class));

	}

	@Test
	public void testSetCheckedConsumerError() throws SQLException {
		ConsumerErrorHandler errorHandler = mock(ConsumerErrorHandler.class);
		ResultSet rs = mock(ResultSet.class);
		when(rs.next()).thenReturn(true, true, false);
		when(rs.getLong(1)).thenReturn(1l);

		final Exception exception = new SQLException("Error!");
		JdbcMapper<DbObject> mapper = JdbcMapperFactoryHelper.asm()
				.consumerErrorHandler(errorHandler)
				.newBuilder(DbObject.class).addMapping("id").mapper();

		mapper.forEach(rs, new CheckedConsumer<DbObject>() {
			@Override
			public void accept(DbObject dbObject) throws Exception {
				throw exception;
			}
		});
		verify(errorHandler, times(2)).handlerError(same(exception), any(DbObject.class));

	}

	@Test
	public void testCustomGetterFactory() throws SQLException {
		JdbcMapper<DbObject> mapper = JdbcMapperFactoryHelper.asm().getterFactory(new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type target, JdbcColumnKey key, Object... properties) {
				return new Getter() {
					@Override
					public Object get(Object target) throws Exception {
						return "Hello!";
					}
				}
						;
			}
		}).newBuilder(DbObject.class).addMapping("name").mapper();

		ResultSet rs = mock(ResultSet.class);
		when(rs.next()).thenReturn(true, false);

		DbObject object = mapper.iterator(rs).next();

		assertEquals("Hello!", object.getName());
	}

	//IFJAVA8_START
	@Test
	public void testCustomPropertyNameMatcher_712() {
		JdbcMapper<DbObject> mapper =
				JdbcMapperFactory
					.newInstance()
					.propertyNameMatcherFactory(DefaultPropertyNameMatcherFactory.DEFAULT.addSeparators('/'))
					.newBuilder(DbObject.class).addMapping("id").addMapping("na/me").mapper();
	}
	//IFJAVA8_END


	private void assertMapPsDbObject(ResultSet rs,
			JdbcMapper<DbObject> mapper) throws Exception,
			ParseException {
		List<DbObject> list = mapper.forEach(rs, new ListCollector<DbObject>()).getList();
		assertEquals(1,  list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
	}
	
	private void assertMapPsDbObjectWithAlias(ResultSet rs,
			JdbcMapper<DbObjectWithAlias> mapper) throws Exception,
			ParseException {
		List<DbObjectWithAlias> list = mapper.forEach(rs, new ListCollector<DbObjectWithAlias>()).getList();
		assertEquals(1,  list.size());
		DbHelper.assertDbObjectWithAliasMapping(list.get(0));
	}

	private void assertMapPsDbObjectWithEmptyAlias(ResultSet rs,
											  JdbcMapper<DbObjectWithEmptyAlias> mapper) throws Exception,
			ParseException {
		List<DbObjectWithEmptyAlias> list = mapper.forEach(rs, new ListCollector<DbObjectWithEmptyAlias>()).getList();
		assertEquals(1,  list.size());
		DbHelper.assertDbObjectWithEmptyAliasMapping(list.get(0));
	}


	private void assertMapPsFinalDbObject(ResultSet rs,
			JdbcMapper<DbFinalObject> mapper) throws Exception,
			ParseException {
		List<DbFinalObject> list = mapper.forEach(rs, new ListCollector<DbFinalObject>()).getList();
		assertEquals(1,  list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
	}


	@Test
	public void testIssue693() throws SQLException {
		Connection dbConnection = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);

		if (dbConnection == null) return;

		try {

			JdbcMapper<Prof693> mapper =  JdbcMapperFactory
					.newInstance()
					.newMapper(Prof693.class);;
			;

			Statement st = dbConnection.createStatement();

			Iterator<Prof693> iterator = mapper.iterator(st.executeQuery("SELECT 1 as id , 'p1' as name, null as students_id, 's1' as students_name, 1::bit as students_test "));

			assertTrue(iterator.hasNext());

			List<Student693> prof693s = Collections.<Student693>emptyList();
			assertEquals(new Prof693(1l, "p1", prof693s), iterator.next());

		} finally {
			dbConnection.close();
		}


	}




	public static class Prof693 {
		@Key
		Long id;
		String name;
		List<Student693> students;

		public Prof693(Long id, String name, List<Student693> students) {
			this.id = id;
			this.name = name;
			this.students = students;
		}

		public Long getId() {
			return id;
		}


		public String getName() {
			return name;
		}


		public List<Student693> getStudents() {
			return students;
		}


		@Override
		public String toString() {
			return "Prof{" +
					"id=" + id +
					", name='" + name + '\'' +
					", students=" + students +
					'}';
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Prof693 prof = (Prof693) o;

			if (id != prof.id) return false;
			if (name != null ? !name.equals(prof.name) : prof.name != null) return false;
			return students != null ? students.equals(prof.students) : prof.students == null;
		}

		@Override
		public int hashCode() {
			int result = (int) (id ^ (id >>> 32));
			result = 31 * result + (name != null ? name.hashCode() : 0);
			result = 31 * result + (students != null ? students.hashCode() : 0);
			return result;
		}
	}

	public static class Student693 {
		@Key
		Long id;
		String name;
		boolean test;


		public Student693(Long id, String name, boolean test) {
			this.id = id;
			this.name = name;
			this.test = test;
		}

		public Long getId() {
			return id;
		}


		public String getName() {
			return name;
		}


		public boolean getTest() {
			return test;
		}


		@Override
		public String toString() {
			return "Student{" +
					"id=" + id +
					", name='" + name + '\'' +
					", test='" + test + '\'' +
					'}';
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Student693 student = (Student693) o;

			if (id != student.id) return false;
			if (test != student.test) return false;
			return name != null ? name.equals(student.name) : student.name == null;
		}

		@Override
		public int hashCode() {
			int result = (int) (id ^ (id >>> 32));
			result = 31 * result + (name != null ? name.hashCode() : 0);
			result = 31 * result + (test ? 1 : 0);
			return result;
		}
	}
}
