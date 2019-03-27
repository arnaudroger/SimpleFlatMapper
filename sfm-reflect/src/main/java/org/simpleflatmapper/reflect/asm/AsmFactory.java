package org.simpleflatmapper.reflect.asm;

import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.BuilderInstantiatorDefinition;
import org.simpleflatmapper.util.FactoryClassLoader;
import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.util.BiFunction;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.util.UnaryFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class AsmFactory {
    private final ConcurrentMap<Class<?>, Object> subFactories = new ConcurrentHashMap<Class<?>, Object>();

    private final FactoryClassLoader factoryClassLoader;
    private final ConcurrentMap<Object, Setter<?, ?>> setterCache = new ConcurrentHashMap<Object, Setter<?, ?>>();
    private final ConcurrentMap<Object, Getter<?, ?>> getterCache = new ConcurrentHashMap<Object, Getter<?, ?>>();
    private final ConcurrentMap<InstantiatorKey, Class<? extends Instantiator<?, ?>>> instantiatorCache = new ConcurrentHashMap<InstantiatorKey, Class<? extends Instantiator<?, ?>>>();
    private final ConcurrentMap<BiInstantiatorKey, Class<? extends BiInstantiator<?, ?, ?>>> biInstantiatorCache = new ConcurrentHashMap<BiInstantiatorKey, Class<? extends BiInstantiator<?, ?, ?>>>();


    public final ClassLoader targetClassLoader;

    public AsmFactory(ClassLoader targetClassLoader) {
        this.targetClassLoader = targetClassLoader;
        this.factoryClassLoader = new FactoryClassLoader(targetClassLoader);
    }

	@SuppressWarnings("unchecked")
	public <T, P> Setter<T,P> createSetter(final Method m) throws Exception {
	    checkClassLoader(m.getDeclaringClass().getClassLoader());
		Setter<T,P> setter = (Setter<T, P>) setterCache.get(m);
		if (setter == null) {
			final String className = generateClassNameForSetter(m);
			final byte[] bytes = generateSetterByteCodes(m, className);
            final Class<?> type = registerClass(className, bytes);
            setter = (Setter<T, P>) type.newInstance();
            setterCache.putIfAbsent(m, setter);
		}
		return setter;
	}

    private Class<?> registerClass(String className, byte[] bytes) {
        return factoryClassLoader.registerClass(className, bytes);
    }

    @SuppressWarnings("unchecked")
    public <T, P> Setter<T,P> createSetter(Field field) throws Exception {
        checkClassLoader(field.getDeclaringClass().getClassLoader());
        Setter<T,P> setter = (Setter<T, P>) setterCache.get(field);
        if (setter == null) {
            final String className = generateClassNameForSetter(field);
            final byte[] bytes = generateSetterByteCodes(field, className);
            final Class<?> type = registerClass(className, bytes);
            setter = (Setter<T, P>) type.newInstance();
            setterCache.putIfAbsent(field, setter);
        }
        return setter;
    }

    private void checkClassLoader(ClassLoader declaringClassLoader) {
        if (declaringClassLoader == null) return; // the class is in system class loader
	    if (targetClassLoader != declaringClassLoader)
	        throw new IllegalArgumentException("This AsmFactory target a different class loader");
    }

    @SuppressWarnings("unchecked")
    public <T, P> Getter<T,P> createGetter(final Method m) throws Exception {
        checkClassLoader(m.getDeclaringClass().getClassLoader());
        Getter<T,P> getter = (Getter<T, P>) getterCache.get(m);
        if (getter == null) {
            final String className = generateClassNameForGetter(m);
            final byte[] bytes = generateGetterByteCodes(m, className);
            final Class<?> type = registerClass(className, bytes);
            getter = (Getter<T, P>) type.newInstance();
            getterCache.putIfAbsent(m, getter);
        }
        return getter;
    }

    @SuppressWarnings("unchecked")
    public <T, P> Getter<T,P> createGetter(final Field m) throws Exception {
        checkClassLoader(m.getDeclaringClass().getClassLoader());
        Getter<T,P> getter = (Getter<T, P>) getterCache.get(m);
        if (getter == null) {
            final String className = generateClassNameForGetter(m);
            final byte[] bytes = generateGetterByteCodes(m, className);
            final Class<?> type = registerClass(className, bytes);
            getter = (Getter<T, P>) type.newInstance();
            getterCache.putIfAbsent(m, getter);
        }
        return getter;
    }

    private byte[] generateGetterByteCodes(final Method m, final String className) throws Exception {
        final Class<?> propertyType = m.getReturnType();
        if (propertyType.isPrimitive()) {
            return GetterBuilder.createPrimitiveGetter(className, m);
        } else {
            return GetterBuilder.createObjectGetter(className, m);
        }
    }

    private byte[] generateGetterByteCodes(final Field m, final String className) throws Exception {
        final Class<?> propertyType = m.getType();
        if (propertyType.isPrimitive()) {
            return GetterBuilder.createPrimitiveGetter(className, m);
        } else {
            return GetterBuilder.createObjectGetter(className, m);
        }
    }

	private byte[] generateSetterByteCodes(final Method m, final String className) throws Exception {
		final Class<?> propertyType = m.getParameterTypes()[0];
		if (propertyType.isPrimitive()) {
			return SetterBuilder.createPrimitiveSetter(className, m);
		} else {
			return SetterBuilder.createObjectSetter(className, m);
		}
	}

    private byte[] generateSetterByteCodes(final Field m, final String className) throws Exception {
        final Class<?> propertyType = m.getType();
        if (propertyType.isPrimitive()) {
            return SetterBuilder.createPrimitiveSetter(className, m);
        } else {
            return SetterBuilder.createObjectSetter(className, m);
        }
    }
	
	@SuppressWarnings("unchecked")
	public <S, T> Instantiator<S, T> createEmptyArgsInstantiator(final Class<S> source, final Class<? extends T> target) throws Exception {
        checkClassLoader(target.getClassLoader());

        InstantiatorKey instantiatorKey = new InstantiatorKey(target, source);
		Class<? extends Instantiator<?, ?>> instantiatorType = instantiatorCache.get(instantiatorKey);
		if (instantiatorType == null) {
			final String className = generateClassNameForInstantiator(instantiatorKey);
			final byte[] bytes = ConstructorBuilder.createEmptyConstructor(className, source, target);
			instantiatorType = (Class<? extends Instantiator<?, ?>>) registerClass(className, bytes);
            instantiatorCache.putIfAbsent(instantiatorKey, instantiatorType);
		}
		return  (Instantiator<S, T>) instantiatorType.newInstance();
	}
	
	@SuppressWarnings("unchecked")
	public <S, T> Instantiator<S, T> createInstantiator(final Class<S> source, final InstantiatorDefinition instantiatorDefinition, final Map<Parameter, Getter<? super S, ?>> injections, boolean builderIgnoresNullValues) throws Exception {

        InstantiatorKey<S> instantiatorKey = new InstantiatorKey<S>(instantiatorDefinition, injections, source);
        checkClassLoader(instantiatorKey.getDeclaringClass().getClassLoader());
        Class<? extends Instantiator<?, ?>> instantiator = instantiatorCache.get(instantiatorKey);
        Instantiator<Void, ?> builderInstantiator = null;
		if (instantiator == null) {
			final String className = generateClassNameForInstantiator(instantiatorKey);
			final byte[] bytes;
            if (instantiatorDefinition instanceof ExecutableInstantiatorDefinition) {
                bytes = InstantiatorBuilder.createInstantiator(className, source, (ExecutableInstantiatorDefinition)instantiatorDefinition, injections);
            }  else {
                builderInstantiator = createInstantiator(Void.class, ((BuilderInstantiatorDefinition)instantiatorDefinition).getBuilderInstantiator(), new HashMap<Parameter, Getter<? super Void, ?>>(), builderIgnoresNullValues);
                bytes = InstantiatorBuilder.createInstantiator(
                        className,
                        source,
                        (BuilderInstantiatorDefinition)instantiatorDefinition, injections, builderIgnoresNullValues);
            }
			instantiator = (Class<? extends Instantiator<?, ?>>) registerClass(className, bytes);
            instantiatorCache.put(instantiatorKey, instantiator);
		}

		Map<String, Getter<? super S, ?>> getterPerName = new HashMap<String, Getter<? super S, ?>>();
		for(Entry<Parameter, Getter<? super S, ?>> e : injections.entrySet()) {
			getterPerName.put(e.getKey().getName(), e.getValue());
		}

        if (instantiatorDefinition instanceof ExecutableInstantiatorDefinition) {
            return (Instantiator<S, T>) instantiator.getConstructor(Map.class).newInstance(getterPerName);
        } else {
            return (Instantiator<S, T>) instantiator.getConstructor(Map.class, Instantiator.class).newInstance(getterPerName, builderInstantiator);
        }
	}


    @SuppressWarnings("unchecked")
    public <S1, S2, T> BiInstantiator<S1, S2, T> createBiInstantiator(final Class<?> s1, final Class<?> s2, final InstantiatorDefinition instantiatorDefinition, final Map<Parameter, BiFunction<? super S1, ? super S2, ?>> injections, boolean builderIgnoresNullValues) throws Exception {
        BiInstantiatorKey instantiatorKey = new BiInstantiatorKey(instantiatorDefinition, injections, s1, s2);

        checkClassLoader(instantiatorKey.getDeclaringClass().getClassLoader());

        Class<? extends BiInstantiator<?, ?, ?>> instantiator = biInstantiatorCache.get(instantiatorKey);
        Instantiator builderInstantiator = null;
        if (instantiator == null) {
            final String className = generateClassNameForBiInstantiator(instantiatorKey);
            final byte[] bytes;
            if (instantiatorDefinition instanceof ExecutableInstantiatorDefinition) {
                bytes = BiInstantiatorBuilder.createInstantiator(className, s1, s2, (ExecutableInstantiatorDefinition)instantiatorDefinition, injections);
            }  else {
                InstantiatorDefinition biii = ((BuilderInstantiatorDefinition) instantiatorDefinition).getBuilderInstantiator();
                builderInstantiator = createInstantiator(Void.class, biii, new HashMap<Parameter, Getter<? super Void, ?>>(), builderIgnoresNullValues);
                bytes = BiInstantiatorBuilder.createInstantiator(
                        className,
                        s1, s2,
                        builderInstantiator,
                        (BuilderInstantiatorDefinition)instantiatorDefinition, injections, builderIgnoresNullValues);
            }
            instantiator = (Class<? extends BiInstantiator<?, ?, ?>>) registerClass(className, bytes);
            biInstantiatorCache.put(instantiatorKey, instantiator);
        }

        Map<String, BiFunction<? super S1, ? super S2, ?>> factoryPerName = new HashMap<String, BiFunction<? super S1, ? super S2, ?>>();
        for(Entry<Parameter, BiFunction<? super S1, ? super S2, ?>> e : injections.entrySet()) {
            factoryPerName.put(e.getKey().getName(), e.getValue());
        }

        if (instantiatorDefinition instanceof ExecutableInstantiatorDefinition) {
            return (BiInstantiator<S1, S2, T>) instantiator.getConstructor(Map.class).newInstance(factoryPerName);
        } else {
            if (builderInstantiator == null) {
                InstantiatorDefinition biii = ((BuilderInstantiatorDefinition) instantiatorDefinition).getBuilderInstantiator();
                builderInstantiator = createInstantiator(Void.class, biii, new HashMap<Parameter, Getter<? super Void, ?>>(), builderIgnoresNullValues);
            }
            return (BiInstantiator<S1, S2, T>) instantiator.getConstructor(Map.class, Instantiator.class).newInstance(factoryPerName, builderInstantiator);
        }
    }
	

    private final AtomicLong classNumber = new AtomicLong();
	
	private String generateClassNameForInstantiator(final InstantiatorKey key) {
		StringBuilder sb = new StringBuilder();
		
		sb.append( "org.simpleflatmapper.reflect.generated.")
		.append(getPackageName(key.getDeclaringClass()))
		.append(".AsmInstantiator").append(key.getDeclaringClass().getSimpleName());
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

    private String generateClassNameForBiInstantiator(final BiInstantiatorKey key) {
        StringBuilder sb = new StringBuilder();

        sb.append( "org.simpleflatmapper.reflect.generated.")
                .append(getPackageName(key.getDeclaringClass()))
                .append(".AsmBiInstantiator").append(key.getDeclaringClass().getSimpleName());
        sb.append("From");
        sb.append(replaceArray(key.getS1().getSimpleName()));
        sb.append("And");
        sb.append(replaceArray(key.getS2().getSimpleName()));

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

	public String replaceArray(String simpleName) {
		return simpleName.replace('[', 's').replace(']', '_');
	}

	private String generateClassNameForSetter(final Method m) {
		return "org.simpleflatmapper.reflect.generated." + (m.getDeclaringClass().getCanonicalName())
					 + "AsmMethodSetter"
                     +"_" + m.getName()+ "_"
					 + replaceArray(m.getParameterTypes()[0].getSimpleName())
					;
	}

    private String generateClassNameForSetter(final Field field) {
        return "org.simpleflatmapper.reflect.generated." + (field.getDeclaringClass().getCanonicalName())
                + "AsmFieldSetter"
                + "_"
                + field.getName()
                + "_"
                + replaceArray(field.getType().getSimpleName())
                ;
    }
    private String generateClassNameForGetter(final Method m) {
        return "org.simpleflatmapper.reflect.generated." + (m.getDeclaringClass().getCanonicalName())
                + "AsmMethodGetter"
                + "_"
                + m.getName()
                ;
    }
    private String generateClassNameForGetter(final Field m) {
        return "org.simpleflatmapper.reflect.generated." + (m.getDeclaringClass().getCanonicalName())
                + "AsmFieldGetter"
                + "_"
                + m.getName()
                ;
    }

    public String getPackageName(Type target) {
        Package targetPackage = TypeHelper.toClass(target).getPackage();
        return targetPackage != null ? targetPackage.getName().isEmpty() ? "none" : targetPackage.getName() : "none";
    }

    public long getNextClassNumber() {
        return classNumber.getAndIncrement();
    }

    public <T> T registerOrCreate(Class<T> clazz, UnaryFactory<AsmFactory, T> factory) {
        T t = clazz.cast(subFactories.get(clazz));

        if (t == null) {
            t = factory.newInstance(this);

            T tPresent = clazz.cast(subFactories.putIfAbsent(clazz, t));

            if (tPresent != null) {
                return tPresent;
            }

        }


        return t;
    }

    public Object createClass(String className, byte[] bytes, ClassLoader classLoader) {
        checkClassLoader(classLoader);
        return registerClass(className, bytes);
    }

}
