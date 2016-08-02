package org.simpleflatmapper.jdbc;

import org.junit.Test;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.core.map.*;
import org.simpleflatmapper.core.reflect.*;
import org.simpleflatmapper.core.reflect.asm.AsmFactory;
import org.simpleflatmapper.core.reflect.InstantiatorFactory;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class JdbcMapperBuilderImplTest {

	@Test
	public void testAddFieldMapper() {
		
		JdbcMapperBuilder<DbObject> builder = JdbcMapperFactoryHelper.asm().newBuilder(DbObject.class);
		
		JdbcMapper<DbObject> mapper = builder.addMapper(new FieldMapper<ResultSet, DbObject>() {
			
			@Override
			public void mapTo(ResultSet source, DbObject target, MappingContext<? super ResultSet> mappingContext) throws MappingException {
				target.setId(33);
			}
		}).mapper();
		
		assertEquals(33, mapper.map(null).getId());
	}

	
	@Test
	public void testAsmFailureOnmapper() {

		JdbcMapperBuilder<DbObject> builder = JdbcMapperFactoryHelper.noFailOnAsm().reflectionService(new ReflectionService(true, true, newAsmFactoryFailsOnmapper())).newBuilder(DbObject.class);

		final JdbcMapper<DbObject> mapper = builder.mapper();
		assertTrue(mapper instanceof JdbcMapperBuilder.StaticJdbcSetRowMapper);
	}

	public AsmFactory newAsmFactoryFailsOnmapper() {
		return new AsmFactory(Thread.currentThread().getContextClassLoader()) {
			@Override
			public <S, T> Mapper<S, T> createMapper(final FieldKey<?>[] keys,
                                                                                final FieldMapper<S, T>[] mappers,
                                                                                final FieldMapper<S, T>[] constructorMappers,
                                                                                final Instantiator<? super S, T> instantiator,
                                                                                final Class<? super S> source,
                                                                                final Class<T> target) throws Exception {
                    throw new UnsupportedOperationException();
                }


                };
	}

	@Test
    public void testAsmFailureOnJdbcMapperFailOnAsm() {
		JdbcMapperBuilder<DbObject> builder = JdbcMapperFactoryHelper.asm().reflectionService(new ReflectionService(true, true, newAsmFactoryFailsOnmapper())).newBuilder(DbObject.class);

        try {
            builder.mapper();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }
	
	public boolean asmPresent = true;
	@Test
	public void testAsmFailureOnInstantiator() {
		JdbcMapperBuilder<DbObject> builder = JdbcMapperFactoryHelper.noFailOnAsm().reflectionService(new ReflectionService(true, true, null) {
			@Override
			public InstantiatorFactory getInstantiatorFactory() {
				return new InstantiatorFactory(null) {
					@Override
					public <S, T> Instantiator<S, T> getInstantiator(Type target,
							Class<?> source,
							List<InstantiatorDefinition> constructors,
							Map<Parameter, Getter<? super S, ?>> injections, boolean useAsm)
							throws SecurityException {
						throw new UnsupportedOperationException();
					}
					
				};
			}
			
			public boolean isAsmPresent() {
				return asmPresent;
			}
		}).newBuilder(DbObject.class);
		
		try {
			builder.mapper();
			fail("Expected exception");
		} catch(UnsupportedOperationException e) {
		}

		asmPresent = false;
		try {
			builder.mapper();
			fail("Expected exception");
		} catch(UnsupportedOperationException e) {
		}
}


}
