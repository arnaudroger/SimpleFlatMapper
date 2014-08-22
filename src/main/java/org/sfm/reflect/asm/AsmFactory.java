package org.sfm.reflect.asm;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.objectweb.asm.Opcodes;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.map.FieldMapper;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.Setter;

public class AsmFactory implements Opcodes {
	private static class FactoryClassLoader extends ClassLoader {

		public FactoryClassLoader(final ClassLoader parent) {
			super(parent);
		}

		private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
		
		@Override
		protected Class<?> findClass(final String name) throws ClassNotFoundException {
			final Class<?> type = classes.get(name);
			
			if (type != null) {
				return type; 
			} else {
				return super.findClass(name);
			}
		}
		
		public Class<?> registerGetter(final String name, final byte[] bytes) {
			Class<?> type = classes.get(name);
			if (type == null) {
				type = defineClass(name, bytes, 0, bytes.length);
				return type;
			} else {
				throw new RuntimeException("Setter " + name + " already defined");
			}
		}
	}
	
	private final FactoryClassLoader factoryClassLoader;
	
	private final Map<Method, Setter<?, ?>> setters = new HashMap<Method, Setter<?, ?>>();
	private final Map<Class<?>, Instantiator<?>> instantiators = new HashMap<Class<?>, Instantiator<?>>();
	
	public AsmFactory() {
		this(Thread.currentThread().getContextClassLoader());
	}
	
	public AsmFactory(ClassLoader cl) {
		factoryClassLoader = new FactoryClassLoader(cl);
	}
	
	@SuppressWarnings("unchecked")
	public <T, P> Setter<T,P> createSetter(final Method m) throws Exception {
		Setter<T,P> setter = (Setter<T, P>) setters.get(m);
		if (setter == null) {
			final String className = generateClassName(m);
			final byte[] bytes = generateClass(m, className);
			final Class<?> type = factoryClassLoader.registerGetter(className, bytes);
			setter = (Setter<T, P>) type.newInstance();
			setters.put(m, setter);
		}
		return setter;
	}

	private byte[] generateClass(final Method m, final String className) throws Exception {
		final Class<?> propertyType = m.getParameterTypes()[0];
		if (AsmUtils.primitivesClassAndWrapper.contains(propertyType)) {
			return SetterBuilder.createPrimitiveSetter(className, m);
		} else {
			return SetterBuilder.createObjectSetter(className, m);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> Instantiator<T> createInstatiantor(final Class<?> target) throws Exception {
		Instantiator<T> instantiator = (Instantiator<T>) instantiators.get(target);
		if (instantiator == null) {
			final String className = generateInstantiatorClassName(target);
			final byte[] bytes = ConstructorBuilder.createEmptyConstructor(className, target);
			final Class<?> type = factoryClassLoader.registerGetter(className, bytes);
			instantiator = (Instantiator<T>) type.newInstance();
			instantiators.put(target, instantiator);
		}
		return instantiator;
	}
	
	@SuppressWarnings("unchecked")
	public <T> JdbcMapper<T> createJdbcMapper(final FieldMapper<ResultSet, T>[] mappers, final Instantiator<T> instantiator, final Class<T> target) throws Exception {
		final String className = generateClassName(mappers, ResultSet.class, target);
		final byte[] bytes = AsmJdbcMapperBuilder.dump(className, mappers, instantiator, target);
		final Class<?> type = factoryClassLoader.registerGetter(className, bytes);
		return (JdbcMapper<T>) type.getDeclaredConstructors()[0].newInstance(mappers, instantiator);
	}
	
	private String generateInstantiatorClassName(final Class<?> target) {
		return "org.sfm.reflect.asm." + target.getPackage().getName() + 
				".AsmInstantiator" + target.getSimpleName();
	}

	private String generateClassName(final Method m) {
		return "org.sfm.reflect.asm." + m.getDeclaringClass().getPackage().getName() + 
					".AsmSetter" + m.getName()
					 + m.getDeclaringClass().getSimpleName()
					 + m.getParameterTypes()[0].getSimpleName()
					;
	}
	private final AtomicLong classNumber = new AtomicLong();
	private <S, T> String generateClassName(final FieldMapper<S, T>[] mappers, final Class<S> source, final Class<T> target) {
		return "org.sfm.reflect.asm." + target.getPackage().getName() + 
					".AsmMapper" + source.getSimpleName() + "2" +  target.getSimpleName() + mappers.length + classNumber.getAndIncrement(); 
	}
}
