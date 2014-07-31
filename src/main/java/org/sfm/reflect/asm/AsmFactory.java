package org.sfm.reflect.asm;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.Setter;

public class AsmFactory implements Opcodes {
	
	
	private static class FactoryClassLoader extends ClassLoader {

		public FactoryClassLoader(ClassLoader parent) {
			super(parent);
		}

		private Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
		
		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			Class<?> type = classes.get(name);
			
			if (type != null) {
				return type; 
			} else {
				return super.findClass(name);
			}
		}
		
		public Class<?> registerGetter(String name, byte[] bytes) {
			Class<?> type = classes.get(name);
			if (type == null) {
				type = defineClass(name, bytes, 0, bytes.length);
				return type;
			} else {
				throw new RuntimeException("Setter " + name + " already defined");
			}
		}
	}
	
	private FactoryClassLoader factoryClassLoader;
	
	private Map<Method, Setter<?, ?>> setters = new HashMap<Method, Setter<?, ?>>();
	private Map<Class<?>, Instantiator<?>> instantiators = new HashMap<Class<?>, Instantiator<?>>();
	
	public AsmFactory() {
		this(Thread.currentThread().getContextClassLoader());
	}
	
	public AsmFactory(ClassLoader cl) {
		factoryClassLoader = new FactoryClassLoader(cl);
	}
	
	@SuppressWarnings("unchecked")
	public <T, P> Setter<T,P> createSetter(Method m) throws Exception {
		Setter<T,P> setter = (Setter<T, P>) setters.get(m);
		if (setter == null) {
			String className = generateClassName(m);
			byte[] bytes = generateClass(m, className);
			Class<?> type = factoryClassLoader.registerGetter(className, bytes);
			setter = (Setter<T, P>) type.newInstance();
			setters.put(m, setter);
		}
		return setter;
	}

	private byte[] generateClass(Method m, String className) throws Exception {
		Class<?> propertyType = m.getParameterTypes()[0];
		if (AsmUtils.primitivesClassAndWrapper.contains(propertyType)) {
			return SetterBuilder.createPrimitiveSetter(className, m);
		} else {
			return SetterBuilder.createObjectSetter(className, m);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> Instantiator<T> createInstatiantor(Class<?> target) throws Exception {
		Instantiator<T> instantiator = (Instantiator<T>) instantiators.get(target);
		if (instantiator == null) {
			String className = generateInstantiatorClassName(target);
			byte[] bytes = ConstructorBuilder.createEmptyConstructor(className, target);
			Class<?> type = factoryClassLoader.registerGetter(className, bytes);
			instantiator = (Instantiator<T>) type.newInstance();
			instantiators.put(target, instantiator);
		}
		return instantiator;
	}
	
	private String generateInstantiatorClassName(Class<?> target) {
		return "org.sfm.reflect.asm." + target.getPackage().getName() + 
				".AsmInstantiator" + target.getSimpleName();
	}

	private String generateClassName(Method m) {
		return "org.sfm.reflect.asm." + m.getDeclaringClass().getPackage().getName() + 
					".AsmSetter" + m.getName()
					 + m.getDeclaringClass().getSimpleName()
					 + m.getParameterTypes()[0].getSimpleName()
					;
	}

}
