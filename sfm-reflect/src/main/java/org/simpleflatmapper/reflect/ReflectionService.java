package org.simpleflatmapper.reflect;

import org.simpleflatmapper.reflect.asm.AsmInstantiatorDefinitionFactory;
import org.simpleflatmapper.reflect.asm.AsmFactory;
import org.simpleflatmapper.reflect.impl.BuilderInstantiatorDefinitionFactory;
import org.simpleflatmapper.reflect.impl.JavaLangClassMetaFactoryProducer;
import org.simpleflatmapper.reflect.instantiator.InstantiatorDefinitions;
import org.simpleflatmapper.reflect.meta.AliasProvider;
import org.simpleflatmapper.reflect.meta.AliasProviderService;
import org.simpleflatmapper.reflect.meta.ArrayClassMeta;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.FastTupleClassMeta;
import org.simpleflatmapper.reflect.meta.MapClassMeta;
import org.simpleflatmapper.reflect.meta.ObjectClassMeta;
//IFJAVA8_START
import org.simpleflatmapper.reflect.meta.OptionalClassMeta;
//IFJAVA8_END
import org.simpleflatmapper.reflect.meta.TupleClassMeta;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.ProducerServiceLoader;
import org.simpleflatmapper.util.TupleHelper;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.util.UnaryFactory;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.simpleflatmapper.util.Asserts.requireNonNull;


public class ReflectionService {

	private static final UnaryFactory<ReflectionService, ClassMeta<?>>[] predefined =
			getPredifinedClassMetaFactory();

	@SuppressWarnings("unchecked")
	private static UnaryFactory<ReflectionService, ClassMeta<?>>[] getPredifinedClassMetaFactory() {
		final List<UnaryFactory<ReflectionService, ClassMeta<?>>> list = new ArrayList<UnaryFactory<ReflectionService, ClassMeta<?>>>();
		Consumer<UnaryFactory<ReflectionService, ClassMeta<?>>> consumer = new Consumer<UnaryFactory<ReflectionService, ClassMeta<?>>>() {
			@Override
			public void accept(UnaryFactory<ReflectionService, ClassMeta<?>> reflectionServiceClassMetaUnaryFactory) {
				list.add(reflectionServiceClassMetaUnaryFactory);
			}
		};

		new JavaLangClassMetaFactoryProducer().produce(consumer);

		ProducerServiceLoader.produceFromServiceLoader(
				ClassMetaFactoryProducer.class,
				consumer
		);

		return list.toArray(new UnaryFactory[0]);
	}

	private final ObjectSetterFactory objectSetterFactory;
	private final ObjectGetterFactory objectGetterFactory;
	private final InstantiatorFactory instantiatorFactory;
	private final AsmFactory asmFactory;
	private final AliasProvider aliasProvider;
	private final boolean builderIgnoresNullValues;
	private final boolean selfScoreFullName;

	private final ConcurrentMap<Type, ClassMeta<?>> metaCache = new ConcurrentHashMap<Type, ClassMeta<?>>();

	public ReflectionService(final AsmFactory asmFactory) {
		this(
				new ObjectSetterFactory(asmFactory),
				new ObjectGetterFactory(asmFactory),
				new InstantiatorFactory(asmFactory),
				asmFactory,
				AliasProviderService.getAliasProvider(),
				true, false);
	}

	private ReflectionService(ObjectSetterFactory objectSetterFactory,
							  ObjectGetterFactory objectGetterFactory,
							  InstantiatorFactory instantiatorFactory,
							  AsmFactory asmFactory,
							  AliasProvider aliasProvider, 
							  boolean builderIgnoresNullValues, 
							  boolean selfScoreFullName) {
		this.objectSetterFactory = objectSetterFactory;
		this.objectGetterFactory = objectGetterFactory;
		this.instantiatorFactory = instantiatorFactory;
		this.asmFactory = asmFactory;
		this.aliasProvider = aliasProvider;
		this.builderIgnoresNullValues = builderIgnoresNullValues;
		this.selfScoreFullName = selfScoreFullName;
		initPredefined();
	}

	private void initPredefined() {
		for (UnaryFactory<ReflectionService, ClassMeta<?>> factory : predefined) {
			ClassMeta<?> classMeta = factory.newInstance(this);
			metaCache.put(classMeta.getType(), classMeta);
		}
	}

	public ObjectSetterFactory getObjectSetterFactory() {
		return objectSetterFactory;
	}

	public InstantiatorFactory getInstantiatorFactory() {
		return instantiatorFactory;
	}


	public boolean isAsmActivated() {
		return asmFactory != null;
	}
	public AsmFactory getAsmFactory() {
		return asmFactory;
	}

	public <T> ClassMeta<T> getClassMeta(Class<T> target) {
		return getClassMeta((Type)target);
	}

	@SuppressWarnings("unchecked")
	public <T> ClassMeta<T> getClassMeta(Type target) {
		requireNonNull("target", target);
		ClassMeta<T> meta = (ClassMeta<T>) metaCache.get(target);
		if (meta == null) {
			meta = newClassMeta(target);
			requireNonNull("meta", meta);
			metaCache.putIfAbsent(target, meta);
		}
		return meta;
	}

	@SuppressWarnings("unchecked")
	private <T> ClassMeta<T> newClassMeta(Type target) {
		Class<T> clazz = TypeHelper.toClass(target);

		if (target instanceof WildcardType) {
			Type[] upperBounds = ((WildcardType) target).getUpperBounds();
			if (upperBounds.length == 1) {
				target = upperBounds[0];
			}
		}
		if (clazz.isArray()) {
			return newArrayMeta(clazz);
			//IFJAVA8_START
		} else if (Optional.class.isAssignableFrom(clazz)) {
			return new OptionalClassMeta(target, this);
			//IFJAVA8_END
		} else if (TupleHelper.isTuple(target)) {
			return new TupleClassMeta<T>(target, this);
		} else if (isFastTuple(clazz)) {
            return new FastTupleClassMeta<T>(target, this);
		} else if (Map.class.isAssignableFrom(clazz)) {
			return (ClassMeta<T>) newMapMeta(target);
		} else if (Collection.class.isAssignableFrom(clazz) || Iterable.class.equals(clazz)) {
			return newCollectionMeta(target);
		}
		return new ObjectClassMeta<T>(target, this);
	}

	public <T> ClassMeta<T> getClassMetaExtraInstantiator(Type target, Member builderInstantiator) {
		return new ObjectClassMeta<T>(target, builderInstantiator, this);
	}

	private <K, V> ClassMeta<Map<K,V>> newMapMeta(Type type) {
		TypeHelper.MapEntryTypes types = TypeHelper.getKeyValueTypeOfMap(type);
		return new MapClassMeta<Map<K, V>, K, V>(type, types.getKeyType(), types.getValueType(), this);
	}
	private <T, E> ClassMeta<T> newArrayMeta(Class<T> clazz) {
		return new ArrayClassMeta<T, E>(clazz, clazz.getComponentType(), this);
	}

	private <T, E> ClassMeta<T> newCollectionMeta(Type type) {
		return new ArrayClassMeta<T, E>(type, TypeHelper.getComponentTypeOfListOrArray(type), this);
	}

	private <T> boolean isFastTuple(Class<T> clazz) {
        Class<?> superClass = clazz.getSuperclass();
        return superClass != null && "com.boundary.tuple.FastTuple".equals(superClass.getName());
    }

	public String getColumnName(Method method) {
		return aliasProvider.getAliasForMethod(method);
	}
	public String getColumnName(Field field) {
		return aliasProvider.getAliasForField(field);
	}

	public List<InstantiatorDefinition> extractInstantiator(Type target) throws IOException {
		return extractInstantiator(target, null);
	}

	public List<InstantiatorDefinition> extractInstantiator(Type target, Member extraInstantiator) throws IOException {
		List<InstantiatorDefinition> list;

        if (!ReflectionInstantiatorDefinitionFactory.areParameterNamePresent(target)) {
            try {
                list = AsmInstantiatorDefinitionFactory.extractDefinitions(target);
            } catch(IOException e) {
                // no access to class file
                list = ReflectionInstantiatorDefinitionFactory.extractDefinitions(target);
            }
		} else {
			list = ReflectionInstantiatorDefinitionFactory.extractDefinitions(target);
		}

		if (extraInstantiator == null) {
			list.addAll(BuilderInstantiatorDefinitionFactory.extractDefinitions(target));
		} else {
			if (extraInstantiator instanceof Method && TypeHelper.areEquals(target, ((Method)extraInstantiator).getGenericReturnType())) {
				// factory method
				list.add(ReflectionInstantiatorDefinitionFactory.definition(((Method)extraInstantiator)));
			} else {
				final BuilderInstantiatorDefinition builder =
						BuilderInstantiatorDefinitionFactory.getDefinitionForBuilder(extraInstantiator, target);
				if (builder == null) {
					throw new IllegalArgumentException("Could not find any setters or build method on builder " + extraInstantiator);
				}
				list.add(builder);
			}
		}

		Collections.sort(list, InstantiatorDefinitions.COMPARATOR);

		return list;
	}

	public static ReflectionService newInstance() {
		return newInstance(true);
	}

	private static final AsmFactory _asmFactory = new AsmFactory(Thread.currentThread().getContextClassLoader());

	public static ReflectionService newInstance(boolean useAsmGeneration) {
		return new ReflectionService(useAsmGeneration && canSeeSetterFromContextClassLoader() ? _asmFactory  : null);
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

    public ObjectGetterFactory getObjectGetterFactory() {
        return objectGetterFactory;
    }

	public boolean hasAsmFactory() {
		return asmFactory != null;
	}


	public ReflectionService withAliasProvider(AliasProvider aliasProvider) {
		return new ReflectionService(
				objectSetterFactory,
				objectGetterFactory,
				instantiatorFactory,
				asmFactory,
				aliasProvider, builderIgnoresNullValues, selfScoreFullName);
	}

	public ReflectionService withBuilderIgnoresNullValues(boolean builderIgnoresNullValues) {
		return new ReflectionService(
				objectSetterFactory,
				objectGetterFactory,
				instantiatorFactory,
				asmFactory,
				aliasProvider, builderIgnoresNullValues, selfScoreFullName);
	}

	public ReflectionService withSelfScoreFullName(boolean selfScoreFullName) {
		return new ReflectionService(
				objectSetterFactory,
				objectGetterFactory,
				instantiatorFactory,
				asmFactory,
				aliasProvider, builderIgnoresNullValues, selfScoreFullName);
	}

	public boolean builderIgnoresNullValues() {
		return builderIgnoresNullValues;
	}
	
	public boolean selfScoreFullName() {
		return selfScoreFullName;
	}

    public interface ClassMetaFactoryProducer extends ProducerServiceLoader.Producer<UnaryFactory<ReflectionService, ClassMeta<?>>> {
	}
}
