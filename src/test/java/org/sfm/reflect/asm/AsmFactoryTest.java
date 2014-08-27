package org.sfm.reflect.asm;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.ResultSet;
import java.util.HashMap;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.beans.DbObject.Type;
import org.sfm.beans.FinalDbObject;
import org.sfm.jdbc.getter.LongIndexedResultSetGetter;
import org.sfm.jdbc.getter.OrdinalEnumIndexedResultSetGetter;
import org.sfm.jdbc.getter.StringIndexedResultSetGetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Instantiator;

public class AsmFactoryTest {

	AsmFactory asmFactory = new AsmFactory();
	
	@Test
	public void testCreateInstatiatorEmptyConstructor() throws Exception {
		Instantiator<ResultSet, DbObject> instantiator = asmFactory.createEmptyArgsInstatiantor(ResultSet.class, DbObject.class);
		assertNotNull(instantiator.newInstance(null));
	}
	@Test
	public void testCreateInstatiatorFinalDbObjectInjectIdAndName() throws Exception {
		@SuppressWarnings("serial")
		Instantiator<ResultSet, FinalDbObject> instantiator = asmFactory.createInstatiantor(ResultSet.class, 
				ConstructorDefinition.extractConstructors(FinalDbObject.class).get(0),
				new HashMap<Parameter, Getter<ResultSet, ?>>() {
					{
						put(new Parameter("id", long.class), new LongIndexedResultSetGetter(1));
						put(new Parameter("name", String.class), new StringIndexedResultSetGetter(2));
					}
				}
				);
		
		ResultSet rs= mock(ResultSet.class);
		when(rs.getLong(1)).thenReturn(33l);
		when(rs.getString(2)).thenReturn("fdo");
		
		
		FinalDbObject fdo = instantiator.newInstance(rs);
		
		assertNotNull(fdo);
		assertNull(fdo.getEmail());
		assertNull(fdo.getCreationTime());
		assertNull(fdo.getTypeName());
		assertNull(fdo.getTypeOrdinal());
		assertEquals(33l, fdo.getId());
		assertEquals("fdo", fdo.getName());
	}
	
	@Test
	public void testCreateInstatiatorFinalDbObjectNameAndType() throws Exception {
		@SuppressWarnings("serial")
		Instantiator<ResultSet, FinalDbObject> instantiator = asmFactory.createInstatiantor(ResultSet.class, 
				ConstructorDefinition.extractConstructors(FinalDbObject.class).get(0),
				new HashMap<Parameter, Getter<ResultSet, ?>>() {
					{
						put(new Parameter("typeOrdinal", Type.class), new OrdinalEnumIndexedResultSetGetter<Type>(1, Type.class));
						put(new Parameter("name", String.class), new StringIndexedResultSetGetter(2));
					}
				}
				);
		
		ResultSet rs= mock(ResultSet.class);
		when(rs.getInt(1)).thenReturn(1);
		when(rs.getString(2)).thenReturn("fdo");
		
		
		FinalDbObject fdo = instantiator.newInstance(rs);
		
		assertNotNull(fdo);
		assertNull(fdo.getEmail());
		assertNull(fdo.getCreationTime());
		assertNull(fdo.getTypeName());
		assertEquals(0, fdo.getId());
		assertEquals("fdo", fdo.getName());
		assertEquals(Type.type2, fdo.getTypeOrdinal());
	}
}
