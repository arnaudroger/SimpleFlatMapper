package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperBuilder;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.map.Mapper;
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

		Mapper<ResultSet, DbObject> mapper1 = getSubMapper(JdbcMapperFactoryHelper.asm().newBuilder(DbObject.class).addMapping("id").addMapping("name").mapper());
		Mapper<ResultSet, DbObject> mapper2 = getSubMapper(JdbcMapperFactoryHelper.asm().newBuilder(DbObject.class).addMapping("id").addMapping("name").mapper());
		final FieldMapperColumnDefinition<JdbcColumnKey> columnDefinition = FieldMapperColumnDefinition.customGetter(new StaticLongGetter<ResultSet>(3));
		Mapper<ResultSet, DbObject> mapper3 = getSubMapper(
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

	private Mapper<ResultSet, DbObject> getSubMapper(JdbcMapper<DbObject> mapper) throws Exception {
		Field field;

		try {
			field = mapper.getClass().getDeclaredField("mapper");
		} catch(NoSuchFieldException e) {
			field = mapper.getClass().getSuperclass().getDeclaredField("mapper");
		}
		field.setAccessible(true);
		return
				new FieldGetter<JdbcMapper<?>, MapperImpl<ResultSet, DbObject>>(field)
						.get(mapper);
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
		builder.addMapping("zone_id", 1, Types.VARCHAR);


		JdbcMapper<C501> mapper = builder.mapper();

		String zoneId = ZoneId.getAvailableZoneIds().iterator().next();
		ResultSet rs = mock(ResultSet.class);
		when(rs.next()).thenReturn(true, false);
		when(rs.getString(1)).thenReturn(zoneId);

		List<C501> l = mapper.forEach(rs, new ListCollector<C501>()).getList();

		assertEquals(1, l.size());
		assertEquals(ZoneId.of(zoneId), l.get(0).zoneId);
	}

	public static class C501 {
		public final ZoneId zoneId;

		public C501(ZoneId zoneId) {
			this.zoneId = zoneId;
		}
	}

	//IFJAVA8_END

}
