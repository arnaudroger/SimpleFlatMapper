package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperBuilder;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.map.mapper.ContextualSourceFieldMapperImpl;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.map.MapperBuilderErrorHandler;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.error.LogFieldMapperErrorHandler;
import org.simpleflatmapper.map.mapper.MapperImpl;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.getter.FieldGetter;
import org.simpleflatmapper.reflect.primitive.LongGetter;
import org.simpleflatmapper.util.ListCollector;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
//IFJAVA8_START
import java.time.ZoneId;
//IFJAVA8_END

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JdbcMapperBuilderTest {


	@Test
	public void testInstantiateBuilderOnType() throws SQLException {
		JdbcMapperBuilder<DbObject> builder = new JdbcMapperBuilder<DbObject>(DbObject.class);
		builder.addMapping("id").addMapping("name");

		final JdbcMapper<DbObject> mapper = builder.mapper();
		List<DbObject> l = mapper.forEach(new MockDbObjectResultSet(1), new ListCollector<DbObject>()).getList();

		assertEquals(1, l.size());
		assertEquals(1, l.get(0).getId());
		assertEquals("name1", l.get(0).getName());

	}
	
	@Test
	public void testWithWrongColumn() throws MappingException, SQLException {
		JdbcMapperBuilder<DbObject> builder = JdbcMapperFactoryHelper.asm().mapperBuilderErrorHandler(MapperBuilderErrorHandler.NULL).newBuilder(DbObject.class);
		builder.addMapping("no_id").addMapping("no_name").addMapping("email");
		
		JdbcMapper<DbObject> mapper = builder.mapper();
		
		List<DbObject> l = mapper.forEach(new MockDbObjectResultSet(1), new ListCollector<DbObject>()).getList();
		
		assertEquals(1, l.size());
		assertEquals(0, l.get(0).getId());
		assertNull(l.get(0).getName());
		assertEquals("email1", l.get(0).getEmail());
	}

	@Test
	public void testAsmFactoryJdbcMapperCache() throws Exception {

		SourceMapper<ResultSet, DbObject> mapper1 = getSubMapper(JdbcMapperFactoryHelper.asm().newBuilder(DbObject.class).addMapping("id").addMapping("name").mapper());
		SourceMapper<ResultSet, DbObject> mapper2 = getSubMapper(JdbcMapperFactoryHelper.asm().newBuilder(DbObject.class).addMapping("id").addMapping("name").mapper());
		final FieldMapperColumnDefinition<JdbcColumnKey> columnDefinition = FieldMapperColumnDefinition.customGetter(new StaticLongGetter<ResultSet>(3));
		SourceMapper<ResultSet, DbObject> mapper3 = getSubMapper(
				JdbcMapperFactoryHelper.asm().fieldMapperErrorHandler(new LogFieldMapperErrorHandler<JdbcColumnKey>()).newBuilder(DbObject.class).addMapping("id",
						columnDefinition).addMapping("name")
						.mapper());

		assertNotSame(mapper1, mapper2);
		assertSame(mapper1.getClass(), mapper2.getClass());
		assertNotSame(mapper1.getClass(), mapper3.getClass());

		assertTrue(mapper1.getClass().getSimpleName().startsWith("AsmMapperFrom"));
		assertTrue(mapper2.getClass().getSimpleName().startsWith("AsmMapperFrom"));
		assertTrue(mapper3.getClass().getSimpleName().startsWith("AsmMapperFrom"));

	}

	private SourceMapper<ResultSet, DbObject> getSubMapper(JdbcMapper<DbObject> mapper) throws Exception {

		Field setRowMapperField = mapper.getClass().getDeclaredField("setRowMapper");
		setRowMapperField.setAccessible(true);
		
		
		Object setRowMapper = setRowMapperField.get(mapper); 
		
		Field mapperField;

		try {
			mapperField = setRowMapper.getClass().getDeclaredField("mapper");
		} catch(NoSuchFieldException e) {
			mapperField = setRowMapper.getClass().getSuperclass().getDeclaredField("mapper");
		}
		mapperField.setAccessible(true);
		SourceMapper<ResultSet, DbObject> mapper1 = new FieldGetter<Object, MapperImpl<ResultSet, DbObject>>(mapperField)
				.get(setRowMapper);
		
		if (mapper1 instanceof ContextualSourceFieldMapperImpl) {
			return ((ContextualSourceFieldMapperImpl)mapper1).getDelegate();
		}
		return
				mapper1;
	}

	static class StaticLongGetter<T> implements LongGetter<T>, Getter<T, Long> {

		private final long l;

		StaticLongGetter(long l) {
			this.l = l;
		}

		@Override
		public Long get(T target) throws Exception {
			return l;
		}

		@Override
		public long getLong(T target) throws Exception {
			return l;
		}
	}

//IFJAVA8_START
	@Test
	public void test501ZoneId() throws MappingException, SQLException {
		test501(JdbcMapperFactoryHelper.asm().newBuilder(C501.class));
		test501(JdbcMapperFactoryHelper.noAsm().newBuilder(C501.class));
	}

	private void test501(JdbcMapperBuilder<C501> builder) throws SQLException {
		
		try {
			builder.addMapping("zone_id", 1, Types.VARCHAR);


			JdbcMapper<C501> mapper = builder.mapper();

			String zoneId = ZoneId.getAvailableZoneIds().iterator().next();
			ResultSet rs = mock(ResultSet.class);
			when(rs.next()).thenReturn(true, false);
			when(rs.getString(1)).thenReturn(zoneId);

			List<C501> l = mapper.forEach(rs, new ListCollector<C501>()).getList();

			assertEquals(1, l.size());
			assertEquals(ZoneId.of(zoneId), l.get(0).zoneId);
		} catch (Throwable e) {
			System.out.println("XXXXXX = " + e);
			e.printStackTrace(System.out);
			throw e;
		}
	}
	
	
	public static class C501 {
		public final ZoneId zoneId;

		public C501(ZoneId zoneId) {
			this.zoneId = zoneId;
		}
	}


	@Test
	public void test509ZoneIdvsStr() throws SQLException {
		JdbcMapperBuilder<C509> builder = JdbcMapperFactoryHelper.noAsm()
				.newBuilder(C509.class);

		builder.addMapping("zoneId", 1, Types.VARCHAR);
		
		JdbcMapper<C509> mapper = builder.mapper();
		
		ResultSet rs = mock(ResultSet.class);
		when(rs.next()).thenReturn(true, false);
		when(rs.getString(1)).thenReturn("UTC");


		List<C509> l = mapper.forEach(rs, new ListCollector<C509>()).getList();

		assertEquals(1, l.size());
		assertEquals("UTC", l.get(0).zoneId.getId());
		

	}


	public static class C509 {
		private ZoneId zoneId;

		public ZoneId getZoneId() {
			return zoneId;
		}

		public void setZoneId(ZoneId zoneId) {
			throw new UnsupportedOperationException();
		}

		public void setZoneId(CharSequence zoneId) {
			this.zoneId = ZoneId.of(zoneId.toString());
		}
	}


	//IFJAVA8_END
	
	
	@Test
	public void test544() {
		final String VALUES_VAL = "values_val";
		final String VALUES = "values";

		JdbcColumnKey valuesKeys = new JdbcColumnKey(VALUES, 1, Types.VARCHAR);

		JdbcMapperFactory
				.newInstance()
				.addAlias(VALUES, VALUES_VAL)
				.newBuilder(C544.class)
				.addMapping(valuesKeys)
				.mapper();
	}

	public static class C544 {
		private final List<String> values;
		public C544(List<String> values) {
			this.values = values;
		}
	}
}
