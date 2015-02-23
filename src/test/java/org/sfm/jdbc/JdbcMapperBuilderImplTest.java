package org.sfm.jdbc;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.jdbc.impl.JdbcMapperImpl;
import org.sfm.map.*;
import org.sfm.reflect.*;
import org.sfm.reflect.asm.AsmFactory;

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
			public void mapTo(ResultSet source, DbObject target, MappingContext mappingContext) throws MappingException {
				target.setId(33);
			}
		}).mapper();
		
		assertEquals(33, mapper.map(null).getId());
	}

	
	@Test
	public void testAsmFailureOnJdbcMapper() {
		
		JdbcMapperBuilder<DbObject> builder = JdbcMapperFactoryHelper.noFailOnAsm().reflectionService(new ReflectionService(true, true, new AsmFactory(Thread.currentThread().getContextClassLoader()) {

            @Override
            public <T> JdbcMapper<T> createJdbcMapper(FieldMapper<ResultSet, T>[] mappers, FieldMapper<ResultSet, T>[] constructorMappers, Instantiator<ResultSet, T> instantiator, Class<T> target, RowHandlerErrorHandler errorHandler) throws Exception {
                throw new UnsupportedOperationException();
            }
		})).newBuilder(DbObject.class);
		
		assertTrue(builder.mapper() instanceof JdbcMapperImpl);
	}

    @Test
    public void testAsmFailureOnJdbcMapperFailOnAsm() {

        JdbcMapperBuilder<DbObject> builder = JdbcMapperFactoryHelper.asm().reflectionService(new ReflectionService(true, true, new AsmFactory(Thread.currentThread().getContextClassLoader()) {

            @Override
            public <T> JdbcMapper<T> createJdbcMapper(FieldMapper<ResultSet, T>[] mappers, FieldMapper<ResultSet, T>[] constructorMappers, Instantiator<ResultSet, T> instantiator, Class<T> target, RowHandlerErrorHandler errorHandler) throws Exception {
                throw new UnsupportedOperationException();
            }
        })).newBuilder(DbObject.class);

        try {
            builder.mapper();
            fail();
        } catch(MapperBuildingException e) {
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
							List<ConstructorDefinition<T>> constructors,
							Map<ConstructorParameter, Getter<S, ?>> injections, boolean useAsm)
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
		} catch(MapperBuildingException e) {
		}

		asmPresent = false;
		try {
			builder.mapper();
			fail("Expected exception");
		} catch(MapperBuildingException e) {
		}
}


}
