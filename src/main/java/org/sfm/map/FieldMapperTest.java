package org.sfm.map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Setter;

public class FieldMapperTest {

	@Test
	public void testErrorHandlerOnGetterError() throws Exception {

		FieldMapperErrorHandler errorHandler = mock(FieldMapperErrorHandler.class);
		final Exception error = new Exception();
		final Object source = new Object();
		final Object target = new Object();
		
		FieldMapper<Object, Object, Object> fieldMapper = new FieldMapper<>(
				"test", new Getter<Object, Object>() {

					@Override
					public Object get(Object target) throws Exception {
						throw error;
					}
				}, new Setter<Object, Object>() {
					@Override
					public void set(Object target, Object value)
							throws Exception {
					}
					@Override
					public Class<? extends Object> getPropertyType() {
						return null;
					}
				}, errorHandler);
		
		fieldMapper.map(source, target);
		
		verify(errorHandler).errorGettingValue("test", source, target, error);
		
	}
	
	@Test
	public void testErrorHandlerOnSetterError() throws Exception {

		FieldMapperErrorHandler errorHandler = mock(FieldMapperErrorHandler.class);
		final Exception error = new Exception();
		final Object source = new Object();
		final Object target = new Object();
		
		FieldMapper<Object, Object, Object> fieldMapper = new FieldMapper<>(
				"test", new Getter<Object, Object>() {

					@Override
					public Object get(Object target) throws Exception {
						return "value";
					}
				}, new Setter<Object, Object>() {
					@Override
					public void set(Object target, Object value)
							throws Exception {
						throw error;
					}
					@Override
					public Class<? extends Object> getPropertyType() {
						return null;
					}
				}, errorHandler);
		
		fieldMapper.map(source, target);
		
		verify(errorHandler).errorSettingValue("test", source, target, error);
		
	}

}
