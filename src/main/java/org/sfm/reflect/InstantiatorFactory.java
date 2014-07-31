package org.sfm.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.sfm.reflect.asm.AsmFactory;

public class InstantiatorFactory {
	private static final Object[] EMPTY_ARGS = new Object[]{};
	
	@SuppressWarnings("serial")
	private static final Map<Class<?>, Object> DEFAULT_VALUES = new HashMap<Class<?>, Object>() {
		{
			put(boolean.class, true);
			put(byte.class, (byte)0);
			put(char.class, (char)0);
			put(short.class, (short)0);
			put(int.class, 0);
			put(long.class, 0l);
			put(float.class, 0f);
			put(double.class, 0.0);
		}
	};
	
	private final AsmFactory asmFactory;
	
	public InstantiatorFactory(AsmFactory asmFactory) {
		this.asmFactory = asmFactory;
	}

	public <T> Instantiator<T> getInstantiator(Class<T> target) throws NoSuchMethodException, SecurityException {
		Constructor<T> constructor = getSmallerConstructor(target);
		
		Object[] args;
		
		if (constructor.getParameterTypes().length == 0) {
			
			if (constructor.isAccessible()) {
				try {
					return asmFactory.createInstatiantor(target);
				} catch (Exception e) {
					// fall back on reflection
				}
			}
			
			args = EMPTY_ARGS;
		} else {
			args = new Object[constructor.getParameterTypes().length];
			for(int i = 0; i < args.length; i++) {
				if (constructor.getParameterTypes()[i].isPrimitive()) {
					args[i] = DEFAULT_VALUES.get(constructor.getParameterTypes()[i]);
				}
			}
		}
		
		constructor.setAccessible(true);
		
		return new ConstructorInstantiator<T>(constructor, args); 
	}

	@SuppressWarnings("unchecked")
	private <T> Constructor<T> getSmallerConstructor(Class<T> target) {
		Constructor<T> selectedConstructor = null;
		
		for(Constructor<?> c : target.getDeclaredConstructors()) {
			if (selectedConstructor == null || (compare(c, selectedConstructor) < 0)) {
				selectedConstructor = (Constructor<T>) c;
			}
		}
		
		return selectedConstructor;
	}
	
	private int compare(Constructor<?> c1, Constructor<?> c2) {
		if (Modifier.isPublic(c1.getModifiers())) {
			if (Modifier.isPublic(c2.getModifiers())) {
				return c1.getParameterTypes().length - c2.getParameterTypes().length;
			} else {
				return -1;
			}
		} else {
			if (Modifier.isPublic(c2.getModifiers())) {
				return 1;
			} else {
				return c1.getParameterTypes().length - c2.getParameterTypes().length;
			}
		}
		
	}
}	
