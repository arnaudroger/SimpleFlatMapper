package org.sfm.reflect;

import com.mysema.query.sql.types.Null;
import org.sfm.reflect.asm.AsmInstantiatorDefinitionFactory;
import org.sfm.reflect.asm.AsmFactory;
import org.sfm.reflect.asm.AsmHelper;
import org.sfm.reflect.meta.*;
import org.sfm.tuples.Tuple2;
import org.sfm.tuples.Tuples;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ReflectionService {

	private final ObjectSetterFactory objectSetterFactory;
    private final ObjectGetterFactory objectGetterFactory;
	private final InstantiatorFactory instantiatorFactory;
	private final AsmFactory asmFactory;
	private final AliasProvider aliasProvider;

	private final boolean asmPresent;
	private final boolean asmActivated;

	private final ConcurrentMap<Type, ClassMeta<?>> metaCache = new ConcurrentHashMap<Type, ClassMeta<?>>();

	public ReflectionService(final boolean asmPresent, final boolean asmActivated, final AsmFactory asmFactory) {
		this.asmPresent = asmPresent;
		this.asmActivated = asmActivated && asmPresent;
		if (asmActivated) {
			this.asmFactory = asmFactory;
		} else {
			this.asmFactory = null;
		}
		this.objectSetterFactory = new ObjectSetterFactory(asmFactory);
        this.objectGetterFactory = new ObjectGetterFactory(asmFactory);
		this.instantiatorFactory = new InstantiatorFactory(asmFactory);
		this.aliasProvider = AliasProviderFactory.getAliasProvider();
	}

	public ObjectSetterFactory getObjectSetterFactory() {
		return objectSetterFactory;
	}

	public InstantiatorFactory getInstantiatorFactory() {
		return instantiatorFactory;
	}

	public boolean isAsmPresent() {
		return asmPresent;
	}

	public boolean isAsmActivated() {
		return asmActivated;
	}
	public AsmFactory getAsmFactory() {
		return asmFactory;
	}

	@SuppressWarnings("unchecked")
	public <T> ClassMeta<T> getClassMeta(Type target) {
		if (target == null) throw new NullPointerException("type is null");
		ClassMeta<T> meta = (ClassMeta<T>) metaCache.get(target);
		if (meta == null) {
			meta = newClassMeta(target);
			if (meta == null) throw new NullPointerException("meta is null");
			metaCache.putIfAbsent(target, meta);
		}
		return meta;
	}

	private <T> ClassMeta<T> newClassMeta(Type target) {
		Class<T> clazz = TypeHelper.toClass(target);

		if (Map.class.isAssignableFrom(clazz)) {
			return (ClassMeta<T>) newMapMeta(target);
		} else if (List.class.isAssignableFrom(clazz)) {
			return newArrayListMeta(target);
		} else if (clazz.isArray()) {
			return newArrayMeta(clazz);
			//IFJAVA8_START
		} else if (Optional.class.isAssignableFrom(clazz)) {
			return new OptionalClassMeta<T>(target, this);
			//IFJAVA8_END
		} else if (Tuples.isTuple(target)) {
			return new TupleClassMeta<T>(target, this);
		} else if (isFastTuple(clazz)) {
            return new FastTupleClassMeta<T>(target, this);
        }
		if (isDirectType(target)) {
			return new DirectClassMeta<T>(target, this);
		} else {
			return new ObjectClassMeta<T>(target, this);
		}
	}

	private <K, V> ClassMeta<Map<K,V>> newMapMeta(Type type) {
		Tuple2<Type, Type> types = TypeHelper.getKeyValueTypeOfMap(type);
		return new MapClassMeta<Map<K, V>, K, V>(type, types.first(), types.second(), this);
	}
	private <T, E> ClassMeta<T> newArrayMeta(Class<T> clazz) {
		return new ArrayClassMeta<T, E>(clazz, clazz.getComponentType(), this);
	}

	private <T, E> ClassMeta<T> newArrayListMeta(Type type) {
		return new ArrayClassMeta<T, E>(ArrayList.class, TypeHelper.getComponentTypeOfListOrArray(type), this);
	}

	private <T> boolean isFastTuple(Class<T> clazz) {
        Class<?> superClass = clazz.getSuperclass();
        return superClass != null && "com.boundary.tuple.FastTuple".equals(superClass.getName());
    }

    private boolean isDirectType(Type target) {
        return TypeHelper.isJavaLang(target)|| TypeHelper.isEnum(target) || TypeHelper.areEquals(target, Date.class);
    }

	public String getColumnName(Method method) {
		return aliasProvider.getAliasForMethod(method);
	}
	public String getColumnName(Field field) {
		return aliasProvider.getAliasForField(field);
	}

	public List<InstantiatorDefinition> extractConstructors(Type target) throws IOException {
		List<InstantiatorDefinition> list;
        if (isAsmPresent()) {
            try {
                list = AsmInstantiatorDefinitionFactory.extractDefinitions(target);
            } catch(IOException e) {
                // no access to class file
                list = ReflectionInstantiatorDefinitionFactory.extractDefinitions(target);
            }
		} else {
			list = ReflectionInstantiatorDefinitionFactory.extractDefinitions(target);
		}

		Collections.sort(list);

		return list;
	}

	public static ReflectionService newInstance() {
		return newInstance(false, true);
	}

	private static final AsmFactory _asmFactory = new AsmFactory(Thread.currentThread().getContextClassLoader());

	public static ReflectionService newInstance(boolean disableAsm, boolean useAsmGeneration) {
		boolean asmPresent = AsmHelper.isAsmPresent() && !disableAsm;
		boolean hasClassLoaderIncapacity = cannotSeeSetterFromContextClassLoader();

		if (hasClassLoaderIncapacity) {
			useAsmGeneration = false;
		}

		return new ReflectionService(asmPresent, useAsmGeneration, asmPresent && useAsmGeneration ? _asmFactory  : null);
	}

	public static ReflectionService disableAsm() {
		return newInstance(true, false);
	}

	private static boolean cannotSeeSetterFromContextClassLoader() {
		try {
			Class.forName(Setter.class.getName(), false, Thread.currentThread().getContextClassLoader());
		} catch(Exception e) {
			return true;
		}

		return false;
	}

    public ObjectGetterFactory getObjectGetterFactory() {
        return objectGetterFactory;
    }
}
 