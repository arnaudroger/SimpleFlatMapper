package org.sfm.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sfm.reflect.asm.AsmFactory;
import org.sfm.reflect.asm.ConstructorDefinition;
import org.sfm.reflect.asm.ConstructorParameter;
import org.sfm.reflect.impl.InjectConstructorInstantiator;
import org.sfm.reflect.impl.StaticConstructorInstantiator;

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
	
	public InstantiatorFactory(final AsmFactory asmFactory) {
		this.asmFactory = asmFactory;
	}

	public <S, T> Instantiator<S, T> getInstantiator(final Class<S> source, final Class<T> target) throws NoSuchMethodException, SecurityException {
		final Constructor<T> constructor = getSmallerConstructor(target);
		
		if (constructor == null) {
			throw new NoSuchMethodException("No available constructor for " + target);
		}
		
		Object[] args;
		
		if (constructor.getParameterTypes().length == 0) {
			
			if (asmFactory != null && Modifier.isPublic(constructor.getModifiers())) {
				try {
					return asmFactory.createEmptyArgsInstatiantor(source, target);
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
		
		return new StaticConstructorInstantiator<S, T>(constructor, args); 
	}
	
	public <S, T> Instantiator<S, T> getInstantiator(final Class<S> source, List<ConstructorDefinition<T>> constructors, Map<ConstructorParameter, Getter<S, ?>> injections) throws NoSuchMethodException, SecurityException {
		final ConstructorDefinition<T> constructorDefinition = getSmallerConstructor(constructors);
		
		Constructor<T> constructor = constructorDefinition.getConstructor();
		
		if (asmFactory != null && Modifier.isPublic(constructor.getModifiers())) {
			try {
				return asmFactory.createInstatiantor(source, constructorDefinition, injections);
			} catch (Exception e) {
				// fall back on reflection
			}
		}
		
		constructor.setAccessible(true);
		
		if (constructor.getParameterTypes().length == 0) {
			return new StaticConstructorInstantiator<S, T>(constructor, EMPTY_ARGS); 
		} else {
			return new InjectConstructorInstantiator<S, T>(constructorDefinition, injections);
		}
	}


	@SuppressWarnings("unchecked")
	private <T> Constructor<T> getSmallerConstructor(final Class<T> target) {
		Constructor<T> selectedConstructor = null;
		
		for(Constructor<?> c : target.getDeclaredConstructors()) {
			if (selectedConstructor == null || (compare(c, selectedConstructor) < 0)) {
				selectedConstructor = (Constructor<T>) c;
			}
		}
		
		return selectedConstructor;
	}

	private <T> ConstructorDefinition<T> getSmallerConstructor(final List<ConstructorDefinition<T>> constructors) {
		ConstructorDefinition<T> selectedConstructor = null;
		
		for(ConstructorDefinition<T> c : constructors) {
			if (selectedConstructor == null || (c.getParameters().length < selectedConstructor.getParameters().length)) {
				selectedConstructor = c;
			}
		}
		
		return selectedConstructor;
	}
	
	private int compare(final Constructor<?> c1, final Constructor<?> c2) {
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
