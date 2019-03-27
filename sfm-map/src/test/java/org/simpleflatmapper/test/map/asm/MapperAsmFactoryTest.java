package org.simpleflatmapper.test.map.asm;

import org.junit.Test;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.test.map.SampleFieldKey;
import org.simpleflatmapper.map.asm.MapperAsmFactory;
import org.simpleflatmapper.reflect.asm.AsmFactory;
import org.simpleflatmapper.test.beans.DbObject;

import java.io.IOException;
import static org.junit.Assert.assertTrue;

public class MapperAsmFactoryTest {

	static MapperAsmFactory asmFactory = new MapperAsmFactory(new AsmFactory(Thread.currentThread().getContextClassLoader()));
	

	@SuppressWarnings("unchecked")
	@Test
	public void testAsmJdbcMapperFailedInstantiator() throws Exception {
		SourceMapper<Object, DbObject> jdbcMapper =
				asmFactory.createMapper(new SampleFieldKey[0],
				(FieldMapper<Object, DbObject>[]) new FieldMapper[]{},
				(FieldMapper<Object, DbObject>[]) new FieldMapper[]{},
				new BiInstantiator<Object, MappingContext<? super Object>, DbObject>() {
					@Override
					public DbObject newInstance(Object s, MappingContext<? super Object> mappingContext) throws Exception {
						throw new IOException("Error");
					}
				},Object.class,
				DbObject.class);
		
		try {
			jdbcMapper.map(null, null);
		} catch(Exception e) {
            assertTrue(e instanceof IOException);
			// ok
		} 
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testAsmJdbcMapperFailedGetter() throws Exception {
		SourceMapper<Object, DbObject> jdbcMapper = asmFactory.createMapper(new SampleFieldKey[0],
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
				new BiInstantiator<Object, MappingContext<? super Object>, DbObject>() {
					@Override
					public DbObject newInstance(Object s, MappingContext<? super Object> context) throws Exception {
						return new DbObject();
					}
				}, Object.class,
				DbObject.class);
		
		try {
			jdbcMapper.map(null, null);
		} catch(MappingException e) {
			// ok
		} 
	}
}
