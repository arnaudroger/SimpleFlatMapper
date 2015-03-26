package org.sfm.reflect.asm;

import org.sfm.csv.CsvColumnKey;
import org.sfm.csv.impl.*;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MappingContextFactory;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.map.FieldMapper;
import org.sfm.reflect.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class AsmFactory {
	private final FactoryClassLoader factoryClassLoader;
	private final ConcurrentMap<Object, Setter<?, ?>> setterCache = new ConcurrentHashMap<Object, Setter<?, ?>>();
    private final ConcurrentMap<Object, Getter<?, ?>> getterCache = new ConcurrentHashMap<Object, Getter<?, ?>>();
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
            final Class<?> type = createClass(className, bytes, m.getDeclaringClass().getClassLoader());
            setter = (Setter<T, P>) type.newInstance();
			setterCache.putIfAbsent(m, setter);
		}
		return setter;
	}

    @SuppressWarnings("unchecked")
    public <T, P> Setter<T,P> createSetter(Field field) throws Exception {
        Setter<T,P> setter = (Setter<T, P>) setterCache.get(field);
        if (setter == null) {
            final String className = generateClassNameForSetter(field);
            final byte[] bytes = generateSetterByteCodes(field, className);
            final Class<?> type = createClass(className, bytes, field.getDeclaringClass().getClassLoader());
            setter = (Setter<T, P>) type.newInstance();
            setterCache.putIfAbsent(field, setter);
        }
        return setter;
    }


    private Class<?> createClass(String className, byte[] bytes, ClassLoader declaringClassLoader) {
        return factoryClassLoader.registerClass(className, bytes, declaringClassLoader);
    }

    @SuppressWarnings("unchecked")
    public <T, P> Getter<T,P> createGetter(final Method m) throws Exception {
        Getter<T,P> getter = (Getter<T, P>) getterCache.get(m);
        if (getter == null) {
            final String className = generateClassNameForGetter(m);
            final byte[] bytes = generateGetterByteCodes(m, className);
            final Class<?> type = createClass(className, bytes, m.getDeclaringClass().getClassLoader());
            getter = (Getter<T, P>) type.newInstance();
            getterCache.putIfAbsent(m, getter);
        }
        return getter;
    }

    @SuppressWarnings("unchecked")
    public <T, P> Getter<T,P> createGetter(final Field m) throws Exception {
        Getter<T,P> getter = (Getter<T, P>) getterCache.get(m);
        if (getter == null) {
            final String className = generateClassNameForGetter(m);
            final byte[] bytes = generateGetterByteCodes(m, className);
            final Class<?> type = createClass(className, bytes, m.getDeclaringClass().getClassLoader());
            getter = (Getter<T, P>) type.newInstance();
            getterCache.putIfAbsent(m, getter);
        }
        return getter;
    }

    private byte[] generateGetterByteCodes(final Method m, final String className) throws Exception {
        final Class<?> propertyType = m.getReturnType();
        if (AsmUtils.primitivesClassAndWrapper.contains(propertyType)) {
            return GetterBuilder.createPrimitiveGetter(className, m);
        } else {
            return GetterBuilder.createObjectGetter(className, m);
        }
    }

    private byte[] generateGetterByteCodes(final Field m, final String className) throws Exception {
        final Class<?> propertyType = m.getType();
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

    private byte[] generateSetterByteCodes(final Field m, final String className) throws Exception {
        final Class<?> propertyType = m.getType();
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
			instantiatorType = (Class<? extends Instantiator<?, ?>>) createClass(className, bytes, target.getClassLoader());
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
			instantiator = (Class<? extends Instantiator<?, ?>>) createClass(className, bytes, constructorDefinition.getConstructor().getDeclaringClass().getClassLoader());
			instantiatorCache.put(instantiatorKey, instantiator);
		}

		Map<String, Getter<S, ?>> getterPerName = new HashMap<String, Getter<S, ?>>();
		for(Entry<ConstructorParameter, Getter<S, ?>> e : injections.entrySet()) {
			getterPerName.put(e.getKey().getName(), e.getValue());
		}

		return (Instantiator<S, T>) instantiator.getConstructor(Map.class).newInstance(getterPerName);
	}
	
	@SuppressWarnings("unchecked")
	public <T> JdbcMapper<T> createJdbcMapper(final FieldMapper<ResultSet, T>[] mappers, final FieldMapper<ResultSet, T>[] constructorMappers, final Instantiator<ResultSet, T> instantiator, final Class<T> target, RowHandlerErrorHandler errorHandler, MappingContextFactory<ResultSet> mappingContextFactory) throws Exception {
		final String className = generateClassNameForJdbcMapper(mappers, constructorMappers, ResultSet.class, target);
		final byte[] bytes = JdbcMapperAsmBuilder.dump(className, mappers, constructorMappers, target);
        final Class<?> type = createClass(className, bytes, target.getClass().getClassLoader());
        final Constructor<?> constructor = type.getDeclaredConstructors()[0];
        return (JdbcMapper<T>) constructor.newInstance(mappers, constructorMappers, instantiator, errorHandler, mappingContextFactory);
	}

    @SuppressWarnings("unchecked")
    public <T> CsvMapperCellHandlerFactory<T> createCsvMapperCellHandler(Type target, DelayedCellSetterFactory<T, ?>[] delayedCellSetterFactories, CellSetter<T>[] setters,
                                                                         Instantiator<CsvMapperCellHandler<T>, T> instantiator, CsvColumnKey[] keys, ParsingContextFactory parsingContextFactory, FieldMapperErrorHandler<CsvColumnKey> fieldErrorHandler,
                                                                         boolean ignoreException
                                                                         ) throws Exception {
        final String className = generateClassCsvMapperCellHandler(target, delayedCellSetterFactories, setters);
        final String factoryName = className + "Factory";
        final byte[] bytes = CsvMapperCellHandlerBuilder.<T>createTargetSetterClass(className, delayedCellSetterFactories, setters, target, ignoreException);
        final byte[] bytesFactory = CsvMapperCellHandlerBuilder.createTargetSetterFactory(factoryName, className, target);
        final Class<?> type = createClass(className, bytes, target.getClass().getClassLoader());
        final Class<?> typeFactory = createClass(factoryName, bytesFactory, target.getClass().getClassLoader());

        return (CsvMapperCellHandlerFactory<T>) typeFactory
                .getConstructor(Instantiator.class, CsvColumnKey[].class, ParsingContextFactory.class, FieldMapperErrorHandler.class)
                .newInstance(instantiator, keys, parsingContextFactory, fieldErrorHandler);


    }

    private <T> String generateClassCsvMapperCellHandler(Type target, DelayedCellSetterFactory<T, ?>[] delayedCellSetterFactories, CellSetter<T>[] setters) {
        StringBuilder sb = new StringBuilder();

        sb.append( "org.sfm.reflect.asm.")
                .append(TypeHelper.toClass(target).getPackage().getName())
                .append(".CsvMapperCellHandler").append(TypeHelper.toClass(target).getSimpleName());
        sb.append("_").append(Integer.toString(delayedCellSetterFactories.length));
        sb.append("_").append(Integer.toString(setters.length));
        sb.append(Long.toHexString(classNumber.getAndIncrement()));
        return sb.toString();    }


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

    private String generateClassNameForSetter(final Field field) {
        return "org.sfm.reflect.asm." + field.getDeclaringClass().getPackage().getName() +
                ".AsmSetter" + field.getName()
                + replaceArray(field.getDeclaringClass().getSimpleName())
                + replaceArray(field.getType().getSimpleName())
                ;
    }
    private String generateClassNameForGetter(final Method m) {
        return "org.sfm.reflect.asm." + m.getDeclaringClass().getPackage().getName() +
                ".AsmGetter" + m.getName()
                + replaceArray(m.getDeclaringClass().getSimpleName())
                ;
    }
    private String generateClassNameForGetter(final Field m) {
        return "org.sfm.reflect.asm." + m.getDeclaringClass().getPackage().getName() +
                ".AsmGetter" + m.getName()
                + replaceArray(m.getDeclaringClass().getSimpleName())
                ;
    }
	private <S, T> String generateClassNameForJdbcMapper(final FieldMapper<S, T>[] mappers,final FieldMapper<S, T>[] mappers2, final Class<S> source, final Class<T> target) {
		return "org.sfm.reflect.asm." + getPackageName(target) +
					".AsmMapper" + replaceArray(source.getSimpleName()) + "2" +  replaceArray(target.getSimpleName()) + mappers.length + "_"+ mappers2.length + "_" + classNumber.getAndIncrement();
	}

    private <T> String getPackageName(Class<T> target) {

        Package targetPackage = target.getPackage();
        return targetPackage != null ? targetPackage.getName() : ".null";
    }

}
