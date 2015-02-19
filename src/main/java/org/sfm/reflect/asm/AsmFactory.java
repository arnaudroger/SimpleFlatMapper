package org.sfm.reflect.asm;

import org.sfm.jdbc.JdbcMapper;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.map.impl.FieldMapper;
import org.sfm.reflect.*;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class AsmFactory {
	private final FactoryClassLoader factoryClassLoader;
	private final ConcurrentMap<Method, Setter<?, ?>> setterCache = new ConcurrentHashMap<Method, Setter<?, ?>>();
    private final ConcurrentMap<Method, Getter<?, ?>> getterCache = new ConcurrentHashMap<Method, Getter<?, ?>>();
	private final ConcurrentMap<InstantiatorKey, Class<? extends Instantiator<?, ?>>> instantiatorCache = new ConcurrentHashMap<InstantiatorKey, Class<? extends Instantiator<?, ?>>>();
	
	public AsmFactory(ClassLoader cl) {
		factoryClassLoader = new FactoryClassLoader(cl);
	}
	
	@SuppressWarnings("unchecked")
	public <T, P> Setter<T,P> createSetter(final Method m) throws Exception {
		Setter<T,P> setter = (Setter<T, P>) setterCache.get(m);
		if (setter == null) {
			final String className = generateClassNameForSetter(m);
			final byte[] bytes = generateSetterByteCodes(m, className);
			final Class<?> type = factoryClassLoader.registerClass(className, bytes);
			setter = (Setter<T, P>) type.newInstance();
			setterCache.putIfAbsent(m, setter);
		}
		return setter;
	}

    public <T, P> Getter<T,P> createGetter(final Method m) throws Exception {
        Getter<T,P> getter = (Getter<T, P>) getterCache.get(m);
        if (getter == null) {
            final String className = generateClassNameForGetter(m);
            final byte[] bytes = generateGetterByteCodes(m, className);
            final Class<?> type = factoryClassLoader.registerClass(className, bytes);
            getter = (Getter<T, P>) type.newInstance();
            getterCache.putIfAbsent(m, getter);
        }
        return getter;
    }

    private byte[] generateGetterByteCodes(final Method m, final String className) throws Exception {
        final Class<?> propertyType = m.getParameterTypes()[0];
        if (AsmUtils.primitivesClassAndWrapper.contains(propertyType)) {
            return GetterBuilder.createPrimitiveGetter(className, m);
        } else {
            return GetterBuilder.createObjectGetter(className, m);
        }
    }

	private byte[] generateSetterByteCodes(final Method m, final String className) throws Exception {
		final Class<?> propertyType = m.getParameterTypes()[0];
		if (AsmUtils.primitivesClassAndWrapper.contains(propertyType)) {
			return SetterBuilder.createPrimitiveSetter(className, m);
		} else {
			return SetterBuilder.createObjectSetter(className, m);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <S, T> Instantiator<S, T> createEmptyArgsInstantiator(final Class<S> source, final Class<? extends T> target) throws Exception {
		InstantiatorKey instantiatorKey = new InstantiatorKey(target, source);
		Class<? extends Instantiator<?, ?>> instantiatorType = instantiatorCache.get(instantiatorKey);
		if (instantiatorType == null) {
			final String className = generateClassNameForInstantiator(instantiatorKey);
			final byte[] bytes = ConstructorBuilder.createEmptyConstructor(className, source, target);
			instantiatorType = (Class<? extends Instantiator<?, ?>>) factoryClassLoader.registerClass(className, bytes);
			instantiatorCache.putIfAbsent(instantiatorKey, instantiatorType);
		}
		return  (Instantiator<S, T>) instantiatorType.newInstance();
	}
	
	@SuppressWarnings("unchecked")
	public <S, T> Instantiator<S, T> createInstantiator(final Class<?> source, final ConstructorDefinition<T> constructorDefinition, final Map<ConstructorParameter, Getter<S, ?>> injections) throws Exception {
		InstantiatorKey instantiatorKey = new InstantiatorKey(constructorDefinition, injections.keySet(), source);
		Class<? extends Instantiator<?, ?>> instantiator = instantiatorCache.get(instantiatorKey);
		if (instantiator == null) {
			final String className = generateClassNameForInstantiator(instantiatorKey);
			final byte[] bytes = InstantiatorBuilder.createInstantiator(className, source, constructorDefinition, injections);
			instantiator = (Class<? extends Instantiator<?, ?>>) factoryClassLoader.registerClass(className, bytes);
			instantiatorCache.put(instantiatorKey, instantiator);
		}

		Map<String, Getter<S, ?>> getterPerName = new HashMap<String, Getter<S, ?>>();
		for(Entry<ConstructorParameter, Getter<S, ?>> e : injections.entrySet()) {
			getterPerName.put(e.getKey().getName(), e.getValue());
		}

		return (Instantiator<S, T>) instantiator.getConstructor(Map.class).newInstance(getterPerName);
	}
	
	@SuppressWarnings("unchecked")
	public <T> JdbcMapper<T> createJdbcMapper(final FieldMapper<ResultSet, T>[] mappers, final Instantiator<ResultSet, T> instantiator, final Class<T> target, RowHandlerErrorHandler errorHandler) throws Exception {
		final String className = generateClassNameForJdbcMapper(mappers, ResultSet.class, target);
		final byte[] bytes = JdbcMapperAsmBuilder.dump(className, mappers, target);
		final Class<?> type = factoryClassLoader.registerClass(className, bytes);
		return (JdbcMapper<T>) type.getDeclaredConstructors()[0].newInstance(mappers, instantiator, errorHandler);
	}
	
	private final AtomicLong classNumber = new AtomicLong();
	
	private String generateClassNameForInstantiator(final InstantiatorKey key) {
		StringBuilder sb = new StringBuilder();
		
		sb.append( "org.sfm.reflect.asm.")
		.append(key.getConstructor().getDeclaringClass().getPackage().getName())
		.append(".AsmInstantiator").append(key.getConstructor().getDeclaringClass().getSimpleName());
		String[] injectedParams = key.getInjectedParams();
		if (injectedParams != null) {
			for(String str : injectedParams) {
				sb.append(str.substring(0, Math.min(str.length(), 3)));
			}
		}
		sb.append(replaceArray(key.getSource().getSimpleName()));
		sb.append(Long.toHexString(classNumber.getAndIncrement()));
		return sb.toString();
	}

	private String replaceArray(String simpleName) {
		return simpleName.replace('[', 's').replace(']', '_');
	}

	private String generateClassNameForSetter(final Method m) {
		return "org.sfm.reflect.asm." + m.getDeclaringClass().getPackage().getName() + 
					".AsmSetter" + m.getName()
					 + replaceArray(m.getDeclaringClass().getSimpleName())
					 + replaceArray(m.getParameterTypes()[0].getSimpleName())
					;
	}

    private String generateClassNameForGetter(final Method m) {
        return "org.sfm.reflect.asm." + m.getDeclaringClass().getPackage().getName() +
                ".AsmGetter" + m.getName()
                + replaceArray(m.getDeclaringClass().getSimpleName())
                ;
    }
	
	private <S, T> String generateClassNameForJdbcMapper(final FieldMapper<S, T>[] mappers, final Class<S> source, final Class<T> target) {
		return "org.sfm.reflect.asm." + target.getPackage().getName() + 
					".AsmMapper" + replaceArray(source.getSimpleName()) + "2" +  replaceArray(target.getSimpleName()) + mappers.length + "_" + classNumber.getAndIncrement(); 
	}
}
