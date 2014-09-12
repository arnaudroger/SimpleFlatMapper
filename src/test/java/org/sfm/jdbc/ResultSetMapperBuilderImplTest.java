package org.sfm.jdbc;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.map.FieldMapper;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.MappingException;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.InstantiatorFactory;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.asm.AsmFactory;
import org.sfm.reflect.asm.ConstructorDefinition;
import org.sfm.reflect.asm.ConstructorParameter;

public class ResultSetMapperBuilderImplTest {

	@Test
	public void testAddFieldMapper() {
		
		ResultSetMapperBuilderImpl<DbObject> builder = new ResultSetMapperBuilderImpl<DbObject>(DbObject.class);
		
		JdbcMapper<DbObject> mapper = builder.addMapper(new FieldMapper<ResultSet, DbObject>() {
			
			@Override
			public void map(ResultSet source, DbObject target) throws MappingException {
				target.setId(33);
			}
		}).mapper();
		
		assertEquals(33, mapper.map(null).getId());
	}

	
	@Test
	public void testAsmFailureOnJdbcMapper() {
		
		ResultSetMapperBuilderImpl<DbObject> builder = new ResultSetMapperBuilderImpl<DbObject>(DbObject.class, new ReflectionService() {

			@Override
			public AsmFactory getAsmFactory() {
				return new AsmFactory() {
					@Override
					public <T> JdbcMapper<T> createJdbcMapper(
							FieldMapper<ResultSet, T>[] mappers,
							Instantiator<ResultSet, T> instantiator,
							Class<T> target, JdbcMapperErrorHandler errorHandler)
							throws Exception {
						throw new UnsupportedOperationException();
					}
					
				};
			}
		});
		
		assertTrue(builder.mapper() instanceof JdbcMapperImpl);
	}
	
	public boolean asmPresent = true;
	@Test
	public void testAsmFailureOnInstantiator() {
		ResultSetMapperBuilderImpl<DbObject> builder = new ResultSetMapperBuilderImpl<DbObject>(DbObject.class, new ReflectionService() {
			@Override
			public InstantiatorFactory getInstantiatorFactory() {
				return new InstantiatorFactory(null) {
					@Override
					public <S, T> Instantiator<S, T> getInstantiator(Class<S> source, Class<? extends T> target)
							throws NoSuchMethodException, SecurityException {
						throw new UnsupportedOperationException();
					}

					@Override
					public <S, T> Instantiator<S, T> getInstantiator(
							Class<S> source,
							List<ConstructorDefinition<T>> constructors,
							Map<ConstructorParameter, Getter<S, ?>> injections)
							throws NoSuchMethodException, SecurityException {
						throw new UnsupportedOperationException();
					}
					
				};
			}
			
			public boolean isAsmPresent() {
				return asmPresent;
			}
		});
		
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
