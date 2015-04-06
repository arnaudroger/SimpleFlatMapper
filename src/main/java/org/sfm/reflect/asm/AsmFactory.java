package org.sfm.reflect.asm;

import org.sfm.csv.CsvColumnKey;
import org.sfm.csv.impl.*;
import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MappingContextFactory;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.map.FieldMapper;
import org.sfm.map.impl.RethrowFieldMapperErrorHandler;
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
    private final ConcurrentMap<JdbcMapperKey, Class<? extends JdbcMapper<?>>> jdbcMapperCache = new ConcurrentHashMap<JdbcMapperKey, Class<? extends JdbcMapper<?>>>();
    private final ConcurrentMap<CsvMapperKey, Class<? extends CsvMapperCellHandlerFactory<?>>> csvMapperCache = new ConcurrentHashMap<CsvMapperKey, Class<? extends CsvMapperCellHandlerFactory<?>>>();

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
	public <S, T> Instantiator<S, T> createInstantiator(final Class<?> source, final ConstructorDefinition<T> constructorDefinition, final Map<Parameter, Getter<S, ?>> injections) throws Exception {
		InstantiatorKey instantiatorKey = new InstantiatorKey(constructorDefinition, injections.keySet(), source);
		Class<? extends Instantiator<?, ?>> instantiator = instantiatorCache.get(instantiatorKey);
		if (instantiator == null) {
			final String className = generateClassNameForInstantiator(instantiatorKey);
			final byte[] bytes = InstantiatorBuilder.createInstantiator(className, source, constructorDefinition, injections);
			instantiator = (Class<? extends Instantiator<?, ?>>) createClass(className, bytes, constructorDefinition.getConstructor().getDeclaringClass().getClassLoader());
			instantiatorCache.put(instantiatorKey, instantiator);
		}

		Map<String, Getter<S, ?>> getterPerName = new HashMap<String, Getter<S, ?>>();
		for(Entry<Parameter, Getter<S, ?>> e : injections.entrySet()) {
			getterPerName.put(e.getKey().getName(), e.getValue());
		}

		return (Instantiator<S, T>) instantiator.getConstructor(Map.class).newInstance(getterPerName);
	}
	
	@SuppressWarnings("unchecked")
	public <T> JdbcMapper<T> createJdbcMapper(final JdbcColumnKey[] keys,
                                                final FieldMapper<ResultSet, T>[] mappers,
                                              final FieldMapper<ResultSet, T>[] constructorMappers,
                                              final Instantiator<ResultSet, T> instantiator,
                                              final Class<T> target,
                                              RowHandlerErrorHandler errorHandler,
                                              MappingContextFactory<ResultSet> mappingContextFactory) throws Exception {

        JdbcMapperKey key = new JdbcMapperKey(keys, mappers, constructorMappers, instantiator, target);
        Class<JdbcMapper<T>> type = (Class<JdbcMapper<T>>) jdbcMapperCache.get(key);
        if (type == null) {

            final String className = generateClassNameForJdbcMapper(mappers, constructorMappers, ResultSet.class, target);
            final byte[] bytes = JdbcMapperAsmBuilder.dump(className, mappers, constructorMappers, target);

            type = (Class<JdbcMapper<T>>) createClass(className, bytes, target.getClass().getClassLoader());
            jdbcMapperCache.put(key, type);
        }
        final Constructor<?> constructor = type.getDeclaredConstructors()[0];
        return (JdbcMapper<T>) constructor.newInstance(mappers, constructorMappers, instantiator, errorHandler, mappingContextFactory);
	}

    @SuppressWarnings("unchecked")
    public <T> CsvMapperCellHandlerFactory<T> createCsvMapperCellHandler(Type target,
                                                                         DelayedCellSetterFactory<T, ?>[] delayedCellSetterFactories, CellSetter<T>[] setters,
                                                                         Instantiator<CsvMapperCellHandler<T>, T> instantiator,
                                                                         CsvColumnKey[] keys,
                                                                         ParsingContextFactory parsingContextFactory,
                                                                         FieldMapperErrorHandler<CsvColumnKey> fieldErrorHandler,
                                                                         int maxMethodSize
                                                                         ) throws Exception {

        CsvMapperKey key = new CsvMapperKey(keys, setters, delayedCellSetterFactories, instantiator, target, fieldErrorHandler, maxMethodSize);

        Class<? extends CsvMapperCellHandlerFactory<?>> typeFactory = csvMapperCache.get(key);

        if (typeFactory == null) {
            final String className = generateClassNameCsvMapperCellHandler(target, delayedCellSetterFactories, setters);
            final String factoryName = className + "Factory";
            final byte[] bytes = CsvMapperCellHandlerBuilder.<T>createTargetSetterClass(className, delayedCellSetterFactories, setters, target, fieldErrorHandler == null || fieldErrorHandler instanceof RethrowFieldMapperErrorHandler, maxMethodSize);
            final byte[] bytesFactory = CsvMapperCellHandlerBuilder.createTargetSetterFactory(factoryName, className, target);
            createClass(className, bytes, target.getClass().getClassLoader());
            typeFactory = (Class<? extends CsvMapperCellHandlerFactory<?>>) createClass(factoryName, bytesFactory, target.getClass().getClassLoader());

            csvMapperCache.put(key, typeFactory);
        }

        return (CsvMapperCellHandlerFactory<T>) typeFactory
                .getConstructor(Instantiator.class, CsvColumnKey[].class, ParsingContextFactory.class, FieldMapperErrorHandler.class)
                .newInstance(instantiator, keys, parsingContextFactory, fieldErrorHandler);


    }

    private final AtomicLong classNumber = new AtomicLong();
	
	private String generateClassNameForInstantiator(final InstantiatorKey key) {
		StringBuilder sb = new StringBuilder();
		
		sb.append( "org.sfm.reflect.asm.")
		.append(getPackageName(key.getConstructor().getDeclaringClass()))
		.append(".AsmInstantiator").append(key.getConstructor().getDeclaringClass().getSimpleName());
        sb.append("From");
        sb.append(replaceArray(key.getSource().getSimpleName()));

        String[] injectedParams = key.getInjectedParams();
        if (injectedParams != null && injectedParams.length > 0) {
            sb.append("Into");
            int e = Math.min(16, injectedParams.length);
            for(int i = 0; i < e; i++) {
                if (i!=0) {
                    sb.append("And");
                }
                sb.append(injectedParams[i]);
            }

            int l = injectedParams.length - e;
            if (l >0) {
                sb.append("And").append(Integer.toString(l)).append("More");
            }
        }
		sb.append("_I").append(Long.toHexString(classNumber.getAndIncrement()));
		return sb.toString();
	}

	private String replaceArray(String simpleName) {
		return simpleName.replace('[', 's').replace(']', '_');
	}

	private String generateClassNameForSetter(final Method m) {
		return "org.sfm.reflect.asm." + getPackageName(m.getDeclaringClass()) +
					".AsmMethodSetter"
					 + replaceArray(m.getDeclaringClass().getSimpleName())
                     +"_" + m.getName()+ "_"
					 + replaceArray(m.getParameterTypes()[0].getSimpleName())
					;
	}

    private String generateClassNameForSetter(final Field field) {
        return "org.sfm.reflect.asm." + getPackageName(field.getDeclaringClass()) +
                ".AsmFieldSetter"
                + replaceArray(field.getDeclaringClass().getSimpleName())
                + "_"
                + field.getName()
                + "_"
                + replaceArray(field.getType().getSimpleName())
                ;
    }
    private String generateClassNameForGetter(final Method m) {
        return "org.sfm.reflect.asm." + getPackageName(m.getDeclaringClass()) +
                ".AsmMethodGetter"
                + replaceArray(m.getDeclaringClass().getSimpleName())
                + "_"
                + m.getName()
                ;
    }
    private String generateClassNameForGetter(final Field m) {
        return "org.sfm.reflect.asm." + getPackageName(m.getDeclaringClass()) +
                ".AsmFieldGetter"
                + replaceArray(m.getDeclaringClass().getSimpleName())
                + "_"
                + m.getName()
                ;
    }

    private <T> String generateClassNameCsvMapperCellHandler(Type target, DelayedCellSetterFactory<T, ?>[] delayedCellSetterFactories, CellSetter<T>[] setters) {
        StringBuilder sb = new StringBuilder();

        sb.append( "org.sfm.reflect.asm.")
                .append(getPackageName(target))
                .append(".AsmCsvMapperCellHandlerTo").append(TypeHelper.toClass(target).getSimpleName());
        if (delayedCellSetterFactories.length > 0) {
            sb.append("DS").append(Integer.toString(delayedCellSetterFactories.length));
        }
        if (setters.length > 0) {
            sb.append("S").append(Integer.toString(setters.length));
        }
        sb.append("_I").append(Long.toHexString(classNumber.getAndIncrement()));
        return sb.toString();
    }

	private <S, T> String generateClassNameForJdbcMapper(final FieldMapper<S, T>[] mappers,final FieldMapper<S, T>[] constructorMappers, final Class<S> source, final Class<T> target) {
        StringBuilder sb = new StringBuilder();

        sb.append("org.sfm.reflect.asm.");
        sb.append(getPackageName(target));
        sb.append(".AsmMapperFrom").append(replaceArray(source.getSimpleName()));
        sb.append("To").append(replaceArray(target.getSimpleName()));

        if (constructorMappers.length > 0) {
            sb.append("ConstInj").append(constructorMappers.length);
        }

        if (mappers.length > 0) {
            sb.append("Inj").append(mappers.length);
        }

        sb.append("_I").append(Long.toHexString(classNumber.getAndIncrement()));

        return sb.toString();
    }

    private <T> String getPackageName(Type target) {

        Package targetPackage = TypeHelper.toClass(target).getPackage();
        return targetPackage != null ? targetPackage.getName() : ".none";
    }

}
