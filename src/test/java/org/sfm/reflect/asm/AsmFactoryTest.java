package org.sfm.reflect.asm;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.rmi.UnexpectedException;
import java.sql.ResultSet;
import java.util.HashMap;

import org.junit.Test;
import org.sfm.beans.DbFinalObject;
import org.sfm.beans.DbObject;
import org.sfm.beans.DbObject.Type;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.jdbc.getter.LongIndexedResultSetGetter;
import org.sfm.jdbc.getter.OrdinalEnumIndexedResultSetGetter;
import org.sfm.jdbc.getter.StringIndexedResultSetGetter;
import org.sfm.map.FieldMapper;
import org.sfm.map.MappingException;
import org.sfm.map.RethrowRowHandlerErrorHandler;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Instantiator;

public class AsmFactoryTest {

	static AsmFactory asmFactory = new AsmFactory();
	
	@Test
	public void testCreateInstatiatorEmptyConstructor() throws Exception {
		Instantiator<ResultSet, DbObject> instantiator = asmFactory.createEmptyArgsInstatiantor(ResultSet.class, DbObject.class);
		assertNotNull(instantiator.newInstance(null));
		assertSame(instantiator, asmFactory.createEmptyArgsInstatiantor(ResultSet.class, DbObject.class));
	}
	@SuppressWarnings("serial")
	@Test
	public void testCreateInstatiatorFinalDbObjectInjectIdAndName() throws Exception {
		Instantiator<ResultSet, DbFinalObject> instantiator = asmFactory.createInstatiantor(ResultSet.class, 
				ConstructorDefinition.extractConstructors(DbFinalObject.class).get(0),
				new HashMap<ConstructorParameter, Getter<ResultSet, ?>>() {
					{
						put(new ConstructorParameter("id", long.class), new LongIndexedResultSetGetter(1));
						put(new ConstructorParameter("name", String.class), new StringIndexedResultSetGetter(2));
					}
				}
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
		
		assertSame(instantiator, asmFactory.createInstatiantor(ResultSet.class, 
				ConstructorDefinition.extractConstructors(DbFinalObject.class).get(0),
				new HashMap<ConstructorParameter, Getter<ResultSet, ?>>() {
					{
						put(new ConstructorParameter("id", long.class), new LongIndexedResultSetGetter(1));
						put(new ConstructorParameter("name", String.class), new StringIndexedResultSetGetter(2));
					}
				}
				));
	}
	
	@Test
	public void testCreateInstatiatorFinalDbObjectNameAndType() throws Exception {
		@SuppressWarnings("serial")
		Instantiator<ResultSet, DbFinalObject> instantiator = asmFactory.createInstatiantor(ResultSet.class, 
				ConstructorDefinition.extractConstructors(DbFinalObject.class).get(0),
				new HashMap<ConstructorParameter, Getter<ResultSet, ?>>() {
					{
						put(new ConstructorParameter("typeOrdinal", Type.class), new OrdinalEnumIndexedResultSetGetter<Type>(1, Type.class));
						put(new ConstructorParameter("name", String.class), new StringIndexedResultSetGetter(2));
					}
				}
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
	public void testAsmJdbcMapperFailedInstantiator() throws NoSuchMethodException, SecurityException, Exception {
		JdbcMapper<DbObject> jdbcMapper = asmFactory.createJdbcMapper(
				(FieldMapper<ResultSet, DbObject>[])new FieldMapper[] {},
				new Instantiator<ResultSet, DbObject>() {
					@Override
					public DbObject newInstance(ResultSet s) throws Exception {
						throw new UnexpectedException("Error");
					}
				}, 
				DbObject.class, new RethrowRowHandlerErrorHandler());
		
		try {
			jdbcMapper.map(null);
		} catch(MappingException e) {
			// ok
		} 
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testAsmJdbcMapperFailedGetter() throws NoSuchMethodException, SecurityException, Exception {
		JdbcMapper<DbObject> jdbcMapper = asmFactory.createJdbcMapper(
				(FieldMapper<ResultSet, DbObject>[])new FieldMapper[] {
						new FieldMapper<ResultSet, DbObject>() {
							@Override
							public void map(ResultSet source, DbObject target)
									throws MappingException {
								throw new MappingException("Expected ", null);
							}
						}
				},
				new Instantiator<ResultSet, DbObject>() {
					@Override
					public DbObject newInstance(ResultSet s) throws Exception {
						return new DbObject();
					}
				}, 
				DbObject.class, new RethrowRowHandlerErrorHandler());
		
		try {
			jdbcMapper.map(null);
		} catch(MappingException e) {
			// ok
		} 
	}
}
