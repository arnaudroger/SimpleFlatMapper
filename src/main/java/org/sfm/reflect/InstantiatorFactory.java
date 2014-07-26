package org.sfm.reflect;

import java.lang.reflect.Constructor;

public class InstantiatorFactory {
	@SuppressWarnings("unchecked")
	public <T> Instantiator<T> getInstantiator(Class<?> target) throws NoSuchMethodException, SecurityException {
		return new ConstructorInstantiator<T>((Constructor<T>)target.getConstructor()); 
	}
}	
