package org.sfm.reflect;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.sfm.reflect.asm.AsmConstructorDefinitionFactory;
import org.sfm.reflect.asm.AsmFactory;
import org.sfm.reflect.asm.AsmHelper;
import org.sfm.reflect.meta.*;
import org.sfm.tuples.Tuples;

public class ReflectionService {

	private final SetterFactory setterFactory;
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
		this.setterFactory = new SetterFactory(asmFactory);
		this.instantiatorFactory = new InstantiatorFactory(asmFactory);
		this.aliasProvider = AliasProviderFactory.getAliasProvider();
	}

	public SetterFactory getSetterFactory() {
		return setterFactory;
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

	public <T, E> ClassMeta<T> getClassMeta(Type target) {
		Class<T> clazz = TypeHelper.toClass(target);
		
		if (List.class.isAssignableFrom(clazz)) {
			ParameterizedType pt = (ParameterizedType) target;
			return new ArrayClassMeta<T, E>(ArrayList.class, pt.getActualTypeArguments()[0], this);
		}else if (clazz.isArray()) {
			return new ArrayClassMeta<T, E>(clazz, clazz.getComponentType(), this);
		}else if (Tuples.isTuple(target)) {
			return new TupleClassMeta<T>(target, this);
		} else if (TypeHelper.isJavaLang(target)) {
			return null;
		}
		return new ObjectClassMeta<T>(target, this);
	}
	public String getColumnName(Method method) {
		return aliasProvider.getAliasForMethod(method);
	}
	public String getColumnName(Field field) {
		return aliasProvider.getAliasForField(field);
	}

	public <T> List<ConstructorDefinition<T>> extractConstructors(Type target) throws IOException {
		List<ConstructorDefinition<T>> list;


		if (isAsmPresent()) {
			list = AsmConstructorDefinitionFactory.extractConstructors(target);
		} else {
			list = ReflectionConstructorDefinitionFactory.extractConstructors(target);
		}
		return list;
	}

	public static ReflectionService newInstance() {
		return newInstance(false, true);
	}

	private static AsmFactory _asmFactory = new AsmFactory(Thread.currentThread().getContextClassLoader());

	public static ReflectionService newInstance(boolean disableAsm, boolean useAsmGeneration) {
		boolean asmPresent = AsmHelper.isAsmPresent() && !disableAsm;
		boolean hasClassLoaderIncapacity = cannotSeeSetterFromContextClassLoader();

		if (hasClassLoaderIncapacity) {
			useAsmGeneration = false;
		}

		return new ReflectionService(asmPresent, useAsmGeneration, asmPresent && useAsmGeneration ? _asmFactory  : null);
	}

	private static boolean cannotSeeSetterFromContextClassLoader() {
		try {
			Class.forName(Setter.class.getName(), false, Thread.currentThread().getContextClassLoader());
		} catch(Exception e) {
			return true;
		}

		return false;
	}

	public static <T> ClassMeta<T> classMeta(Type target) {
		return newInstance().getClassMeta(target);
	}
}
 