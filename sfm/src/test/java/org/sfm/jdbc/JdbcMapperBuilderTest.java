package org.sfm.jdbc;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.map.Mapper;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MappingException;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.error.LogFieldMapperErrorHandler;
import org.sfm.map.mapper.MapperImpl;
import org.sfm.reflect.Getter;
import org.sfm.reflect.impl.FieldGetter;
import org.sfm.reflect.primitive.LongGetter;
import org.sfm.utils.ListHandler;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

public class JdbcMapperBuilderTest {

	@Test
	public void testInstantiateBuilderOnType() throws SQLException {
		JdbcMapperBuilder<DbObject> builder = new JdbcMapperBuilder<DbObject>(DbObject.class);
		builder.addMapping("id").addMapping("name");

		final JdbcMapper<DbObject> mapper = builder.mapper();
		List<DbObject> l = mapper.forEach(new MockDbObjectResultSet(1), new ListHandler<DbObject>()).getList();

		assertEquals(1, l.size());
		assertEquals(1, l.get(0).getId());
		assertEquals("name1", l.get(0).getName());

	}
	
	@Test
	public void testWithWrongColumn() throws MappingException, SQLException {
		JdbcMapperBuilder<DbObject> builder = JdbcMapperFactoryHelper.asm().mapperBuilderErrorHandler(MapperBuilderErrorHandler.NULL).newBuilder(DbObject.class);
		builder.addMapping("no_id").addMapping("no_name").addMapping("email");
		
		JdbcMapper<DbObject> mapper = builder.mapper();
		
		List<DbObject> l = mapper.forEach(new MockDbObjectResultSet(1), new ListHandler<DbObject>()).getList();
		
		assertEquals(1, l.size());
		assertEquals(0, l.get(0).getId());
		assertNull(l.get(0).getName());
		assertEquals("email1", l.get(0).getEmail());
	}

	@Test
	public void testAsmFactoryJdbcMapperCache() throws Exception {

		Mapper<ResultSet, DbObject> mapper1 = getSubMapper(JdbcMapperFactoryHelper.asm().newBuilder(DbObject.class).addMapping("id").addMapping("name").mapper());
		Mapper<ResultSet, DbObject> mapper2 = getSubMapper(JdbcMapperFactoryHelper.asm().newBuilder(DbObject.class).addMapping("id").addMapping("name").mapper());
		final FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> columnDefinition = FieldMapperColumnDefinition.customGetter(new StaticLongGetter<ResultSet>(3));
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
}
