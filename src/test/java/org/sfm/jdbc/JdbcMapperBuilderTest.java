package org.sfm.jdbc;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MappingException;
import org.sfm.map.impl.FieldMapperColumnDefinition;
import org.sfm.reflect.Getter;
import org.sfm.utils.ListHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

public class JdbcMapperBuilderTest {

	
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
	public void testAsmFactoryJdbcMapperCache() {

		JdbcMapper<DbObject> mapper1 = JdbcMapperFactoryHelper.asm().newBuilder(DbObject.class).addMapping("id").addMapping("name").mapper();
		JdbcMapper<DbObject> mapper2 = JdbcMapperFactoryHelper.asm().newBuilder(DbObject.class).addMapping("id").addMapping("name").mapper();
		JdbcMapper<DbObject> mapper3 =
				JdbcMapperFactoryHelper.asm().newBuilder(DbObject.class).addMapping("id",
						FieldMapperColumnDefinition.customGetter(new Getter<ResultSet, Long>() {
							@Override
							public Long get(ResultSet target) throws Exception {
								return 3l;
							}
						})).addMapping("name").mapper();

		assertNotSame(mapper1, mapper2);
		assertSame(mapper1.getClass(), mapper2.getClass());
		assertNotSame(mapper1.getClass(), mapper3.getClass());

		assertTrue(mapper1.getClass().getSimpleName().startsWith("AsmMapperFrom"));
		assertTrue(mapper2.getClass().getSimpleName().startsWith("AsmMapperFrom"));
		assertTrue(mapper3.getClass().getSimpleName().startsWith("AsmMapperFrom"));

	}
}
