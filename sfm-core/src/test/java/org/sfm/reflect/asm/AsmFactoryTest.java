package org.sfm.reflect.asm;

import org.junit.Test;
import org.sfm.beans.DbFinalObject;
import org.sfm.beans.DbObject;
import org.sfm.beans.DbObject.Type;
import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.jdbc.impl.getter.IntResultSetGetter;
import org.sfm.jdbc.impl.getter.LongResultSetGetter;
import org.sfm.jdbc.impl.getter.StringResultSetGetter;
import org.sfm.map.Mapper;
import org.sfm.map.MappingContext;
import org.sfm.map.MappingException;
import org.sfm.map.FieldMapper;
import org.sfm.map.getter.OrdinalEnumGetter;
import org.sfm.reflect.ExecutableInstantiatorDefinition;
import org.sfm.reflect.InstantiatorDefinition;
import org.sfm.reflect.Parameter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Instantiator;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AsmFactoryTest {

	static AsmFactory asmFactory = new AsmFactory(Thread.currentThread().getContextClassLoader());
	
	@Test
	public void testCreateInstantiatorEmptyConstructor() throws Exception {
		Instantiator<ResultSet, DbObject> instantiator = asmFactory.createEmptyArgsInstantiator(ResultSet.class, DbObject.class);
		assertNotNull(instantiator.newInstance(null));
		assertSame(instantiator.getClass(), asmFactory.createEmptyArgsInstantiator(ResultSet.class, DbObject.class).getClass());
	}
	@Test
	public void testCreateInstantiatorFinalDbObjectInjectIdAndName() throws Exception {
		ExecutableInstantiatorDefinition instantiatorDefinition =
				(ExecutableInstantiatorDefinition) AsmInstantiatorDefinitionFactory.extractDefinitions(DbFinalObject.class).get(0);
		HashMap<Parameter, Getter<? super ResultSet, ?>> injections = new HashMap<Parameter, Getter<? super ResultSet, ?>>();
		injections.put(new Parameter(0, "id", long.class), new LongResultSetGetter(1));
		injections.put(new Parameter(1, "name", String.class), new StringResultSetGetter(2));
		Instantiator<ResultSet, DbFinalObject> instantiator = asmFactory.createInstantiator(ResultSet.class,
				instantiatorDefinition,
				injections
		);
		
		ResultSet rs= mock(ResultSet.class);
		when(rs.getLong(1)).thenReturn(33l);
		when(rs.getString(2)).thenReturn("fdo");
		
		
		DbFinalObject fdo = instantiator.newInstance(rs);
		
		assertNotNull(fdo);
		assertNull(fdo.getEmail());
		assertNull(fdo.getCreationTime());
		assertNull(fdo.getTypeName());
		assertNull(fdo.getTypeOrdinal());
		assertEquals(33l, fdo.getId());
		assertEquals("fdo", fdo.getName());


		assertSame(instantiator.getClass(), asmFactory.createInstantiator(ResultSet.class,
				instantiatorDefinition,
				injections
		).getClass());
	}
	
	@Test
	public void testCreateInstantiatorFinalDbObjectNameAndType() throws Exception {
		HashMap<Parameter, Getter<? super ResultSet, ?>> injections = new HashMap<Parameter, Getter<? super ResultSet, ?>>();
		injections.put(new Parameter(4, "typeOrdinal", Type.class), new OrdinalEnumGetter<ResultSet, Type>(new IntResultSetGetter(1), Type.class));
		injections.put(new Parameter(1, "name", String.class), new StringResultSetGetter(2));

		List<InstantiatorDefinition> instantiatorDefinitions = AsmInstantiatorDefinitionFactory.extractDefinitions(DbFinalObject.class);
		Instantiator<ResultSet, DbFinalObject> instantiator = asmFactory.createInstantiator(ResultSet.class,
				(ExecutableInstantiatorDefinition) instantiatorDefinitions.get(0),
				injections
		);
		
		ResultSet rs= mock(ResultSet.class);
		when(rs.getInt(1)).thenReturn(1);
		when(rs.getString(2)).thenReturn("fdo");
		
		
		DbFinalObject fdo = instantiator.newInstance(rs);
		
		assertNotNull(fdo);
		assertNull(fdo.getEmail());
		assertNull(fdo.getCreationTime());
		assertNull(fdo.getTypeName());
		assertEquals(0, fdo.getId());
		assertEquals("fdo", fdo.getName());
		assertEquals(Type.type2, fdo.getTypeOrdinal());
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void testAsmJdbcMapperFailedInstantiator() throws Exception {
		Mapper<ResultSet, DbObject> jdbcMapper =
				asmFactory.createMapper(new JdbcColumnKey[0],
				(FieldMapper<ResultSet, DbObject>[]) new FieldMapper[]{},
				(FieldMapper<ResultSet, DbObject>[]) new FieldMapper[]{},
				new Instantiator<ResultSet, DbObject>() {
					@Override
					public DbObject newInstance(ResultSet s) throws Exception {
						throw new IOException("Error");
					}
				},ResultSet.class,
				DbObject.class);
		
		try {
			jdbcMapper.map(null);
		} catch(Exception e) {
            assertTrue(e instanceof IOException);
			// ok
		} 
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testAsmJdbcMapperFailedGetter() throws Exception {
		Mapper<ResultSet, DbObject> jdbcMapper = asmFactory.createMapper(new JdbcColumnKey[0],
				(FieldMapper<ResultSet, DbObject>[]) new FieldMapper[]{
						new FieldMapper<ResultSet, DbObject>() {
							@Override
							public void mapTo(ResultSet source, DbObject target, MappingContext<? super ResultSet> mappingContext)
									throws MappingException {
								throw new MappingException("Expected ");
							}
						}
				},
				(FieldMapper<ResultSet, DbObject>[]) new FieldMapper[]{},
				new Instantiator<ResultSet, DbObject>() {
					@Override
					public DbObject newInstance(ResultSet s) throws Exception {
						return new DbObject();
					}
				}, ResultSet.class,
				DbObject.class);
		
		try {
			jdbcMapper.map(null);
		} catch(MappingException e) {
			// ok
		} 
	}
}
