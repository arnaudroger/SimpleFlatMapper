package org.sfm.reflect.asm;

import org.junit.Test;
import org.sfm.beans.DbFinalObject;
import org.sfm.beans.DbObject;
import org.sfm.beans.DbObject.Type;
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
import org.sfm.reflect.impl.ConstantGetter;
import org.sfm.reflect.impl.ConstantIntGetter;
import org.sfm.reflect.impl.ConstantLongGetter;
import org.sfm.samples.SampleFieldKey;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class AsmFactoryTest {

	static AsmFactory asmFactory = new AsmFactory(Thread.currentThread().getContextClassLoader());
	
	@Test
	public void testCreateInstantiatorEmptyConstructor() throws Exception {
		Instantiator<Object, DbObject> instantiator = asmFactory.createEmptyArgsInstantiator(Object.class, DbObject.class);
		assertNotNull(instantiator.newInstance(null));
		assertSame(instantiator.getClass(), asmFactory.createEmptyArgsInstantiator(Object.class, DbObject.class).getClass());
	}
	@Test
	public void testCreateInstantiatorFinalDbObjectInjectIdAndName() throws Exception {
		ExecutableInstantiatorDefinition instantiatorDefinition =
				(ExecutableInstantiatorDefinition) AsmInstantiatorDefinitionFactory.extractDefinitions(DbFinalObject.class).get(0);
		HashMap<Parameter, Getter<? super Object, ?>> injections = new HashMap<Parameter, Getter<? super Object, ?>>();
		injections.put(new Parameter(0, "id", long.class), new ConstantLongGetter<Object>(33l));
		injections.put(new Parameter(1, "name", String.class), new ConstantGetter<Object, String>("fdo"));
		Instantiator<Object, DbFinalObject> instantiator = asmFactory.createInstantiator(Object.class,
				instantiatorDefinition,
				injections
		);
		

		DbFinalObject fdo = instantiator.newInstance(new Object());
		
		assertNotNull(fdo);
		assertNull(fdo.getEmail());
		assertNull(fdo.getCreationTime());
		assertNull(fdo.getTypeName());
		assertNull(fdo.getTypeOrdinal());
		assertEquals(33l, fdo.getId());
		assertEquals("fdo", fdo.getName());


		assertSame(instantiator.getClass(), asmFactory.createInstantiator(Object.class,
				instantiatorDefinition,
				injections
		).getClass());
	}
	
	@Test
	public void testCreateInstantiatorFinalDbObjectNameAndType() throws Exception {
		HashMap<Parameter, Getter<? super Object, ?>> injections = new HashMap<Parameter, Getter<? super Object, ?>>();
		injections.put(new Parameter(4, "typeOrdinal", Type.class), new OrdinalEnumGetter<Object, Type>(new ConstantIntGetter<Object>(1), Type.class));
		injections.put(new Parameter(1, "name", String.class), new ConstantGetter<Object, String>("fdo"));

		List<InstantiatorDefinition> instantiatorDefinitions = AsmInstantiatorDefinitionFactory.extractDefinitions(DbFinalObject.class);
		Instantiator<Object, DbFinalObject> instantiator = asmFactory.createInstantiator(Object.class,
				(ExecutableInstantiatorDefinition) instantiatorDefinitions.get(0),
				injections
		);
		
		DbFinalObject fdo = instantiator.newInstance(new Object());
		
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
		Mapper<Object, DbObject> jdbcMapper =
				asmFactory.createMapper(new SampleFieldKey[0],
				(FieldMapper<Object, DbObject>[]) new FieldMapper[]{},
				(FieldMapper<Object, DbObject>[]) new FieldMapper[]{},
				new Instantiator<Object, DbObject>() {
					@Override
					public DbObject newInstance(Object s) throws Exception {
						throw new IOException("Error");
					}
				},Object.class,
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
		Mapper<Object, DbObject> jdbcMapper = asmFactory.createMapper(new SampleFieldKey[0],
				(FieldMapper<Object, DbObject>[]) new FieldMapper[]{
						new FieldMapper<Object, DbObject>() {
							@Override
							public void mapTo(Object source, DbObject target, MappingContext<? super Object> mappingContext)
									throws MappingException {
								throw new MappingException("Expected ");
							}
						}
				},
				(FieldMapper<Object, DbObject>[]) new FieldMapper[]{},
				new Instantiator<Object, DbObject>() {
					@Override
					public DbObject newInstance(Object s) throws Exception {
						return new DbObject();
					}
				}, Object.class,
				DbObject.class);
		
		try {
			jdbcMapper.map(null);
		} catch(MappingException e) {
			// ok
		} 
	}
}
