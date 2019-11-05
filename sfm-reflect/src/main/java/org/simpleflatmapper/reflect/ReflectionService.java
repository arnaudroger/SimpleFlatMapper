package org.simpleflatmapper.reflect;

import org.simpleflatmapper.reflect.asm.AsmFactory;
import org.simpleflatmapper.reflect.asm.AsmFactoryProvider;
import org.simpleflatmapper.reflect.meta.AliasProvider;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.util.BiConsumer;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.ProducerServiceLoader;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.util.UnaryFactory;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.*;
import java.util.*;


public abstract class ReflectionService implements AsmFactoryProvider {

	
	public abstract void registerClassMeta(Type type, ClassMeta<?> classMeta);

	public abstract ObjectSetterFactory getObjectSetterFactory();
	public abstract ObjectGetterFactory getObjectGetterFactory();
	public abstract InstantiatorFactory getInstantiatorFactory();


	public abstract boolean isAsmActivated();

	public final <T> ClassMeta<T> getClassMeta(Class<T> target) {
		return getClassMeta((Type)target);
	}

	public abstract <T> ClassMeta<T> getClassMeta(Type target);


	public abstract <T> ClassMeta<T> getClassMetaExtraInstantiator(Type target, Member builderInstantiator);


	public abstract String getColumnName(Method method);
	public abstract String getColumnName(Field field);

	public final List<InstantiatorDefinition> extractInstantiator(Type target) throws IOException {
		return extractInstantiator(target, null);
	}

	public abstract List<InstantiatorDefinition> extractInstantiator(Type target, Member extraInstantiator) throws IOException;

	public abstract ReflectionService withAliasProvider(AliasProvider aliasProvider);
	public abstract ReflectionService withBuilderIgnoresNullValues(boolean builderIgnoresNullValues);

	@Deprecated
	public abstract ReflectionService withSelfScoreFullName(boolean selfScoreFullName);

	public abstract boolean builderIgnoresNullValues();

	@Deprecated
	public abstract boolean selfScoreFullName();

	public abstract void registerBuilder(String name, DefaultBuilderSupplier defaultBuilderSupplier);



	public static ReflectionService newInstance() {
		return newInstance(true);
	}

	private static final AsmFactory _defaultAsmFactory = new AsmFactory(ReflectionService.class.getClassLoader());

	public static ReflectionService newInstance(boolean useAsmGeneration) {
		return new DefaultReflectionService(useAsmGeneration && canSeeSetterFromContextClassLoader() ? _defaultAsmFactory : null);
	}

	public static ReflectionService disableAsm() {
		return newInstance(false);
	}

	private static boolean canSeeSetterFromContextClassLoader() {
		try {
			Class.forName(Setter.class.getName(), false, Thread.currentThread().getContextClassLoader());
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	public interface ClassMetaFactoryProducer extends ProducerServiceLoader.Producer<UnaryFactory<ReflectionService, ClassMeta<?>>> {
	}

	public interface BuilderProducer extends ProducerServiceLoader.Producer<Consumer<BiConsumer<String, UnaryFactory<Type, Member>>>> {
		
	}


	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE})
	public @interface PassThrough {
		String value() default "value";
	}


	public static class DefaultBuilderSupplier implements UnaryFactory<Type, Member> {

		private final String clazzName;
		private final String methodName;

		public DefaultBuilderSupplier(String clazzName, String methodName) {
			this.clazzName = clazzName;
			this.methodName = methodName;
		}

		@Override
		public Member newInstance(Type type) {
			try {
				Class<?> builderClazz = TypeHelper.toClass(type).getClassLoader().loadClass(clazzName);
				if (methodName != null) {
					return builderClazz.getMethod(methodName);
				} else {
					return builderClazz.getConstructor();
				}
			} catch (ClassNotFoundException e) {
				return ErrorHelper.rethrow(e);
			} catch (NoSuchMethodException e) {
				return ErrorHelper.rethrow(e);
			}
		}
	}
}
