package org.sfm.reflect;

import org.sfm.reflect.asm.AsmInstantiatorDefinitionFactory;
import org.sfm.reflect.asm.AsmFactory;
import org.sfm.reflect.asm.AsmHelper;
import org.sfm.reflect.meta.*;
import org.sfm.tuples.Tuples;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class ReflectionService {

	private final ObjectSetterFactory objectSetterFactory;
    private final ObjectGetterFactory objectGetterFactory;
	private final InstantiatorFactory instantiatorFactory;
	private final AsmFactory asmFactory;
	private final AliasProvider aliasProvider;

	private final boolean asmPresent;
	private final boolean asmActivated;

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

	public <T, E> ClassMeta<T> getClassMeta(Type target, boolean root) {
		Class<T> clazz = TypeHelper.toClass(target);
		
		if (List.class.isAssignableFrom(clazz)) {
			ParameterizedType pt = (ParameterizedType) target;
			return new ArrayClassMeta<T, E>(ArrayList.class, pt.getActualTypeArguments()[0], this);
		} else if (clazz.isArray()) {
			return new ArrayClassMeta<T, E>(clazz, clazz.getComponentType(), this);
			//JAVA8_START
		} else if (Optional.class.isAssignableFrom(clazz)) {
			return new OptionalClassMeta<T>(target, this);
			//JAVA8_END
		} else if (Tuples.isTuple(target)) {
			return new TupleClassMeta<T>(target, this);
		} else if (isFastTuple(clazz)) {
            return new FastTupleClassMeta<T>(target, this);
        }
		if (root) {
            if (isDirectType(target)) {
                return new DirectClassMeta<T>(target, this);
            } else {
                return new SingletonClassMeta<T>(new ObjectClassMeta<T>(target, this));
            }
		} else {
			if (isDirectType(target)) {
				return null;
			} else {
				return new ObjectClassMeta<T>(target, this);
			}
		}
	}

    private <T> boolean isFastTuple(Class<T> clazz) {
        Class<?> superClass = clazz.getSuperclass();
        return superClass != null && "com.boundary.tuple.FastTuple".equals(superClass.getName());
    }

    private boolean isDirectType(Type target) {
        return TypeHelper.isJavaLang(target)|| TypeHelper.isEnum(target) || TypeHelper.areEquals(target, Date.class);
    }

    public <T> ClassMeta<T> getRootClassMeta(Type mapToClass) {
		return getClassMeta(mapToClass, true);
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
 