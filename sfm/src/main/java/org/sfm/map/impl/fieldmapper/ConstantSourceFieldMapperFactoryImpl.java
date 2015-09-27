package org.sfm.map.impl.fieldmapper;

import org.sfm.map.*;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.context.MappingContextFactoryBuilder;
import org.sfm.map.mapper.PropertyMapping;
import org.sfm.map.mapper.TypeAffinity;
import org.sfm.reflect.*;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.PropertyMeta;

import java.lang.reflect.Type;

public final class ConstantSourceFieldMapperFactoryImpl<S, K extends FieldKey<K>> implements ConstantSourceFieldMapperFactory<S,K> {

	private final GetterFactory<? super S, K> getterFactory;

	public ConstantSourceFieldMapperFactoryImpl(GetterFactory<? super S, K> getterFactory) {
		this.getterFactory = getterFactory;
	}


	private <T, P> FieldMapper<S, T> primitiveIndexedFieldMapper(final Class<P> type, final Setter<T, ? super P> setter, final Getter<? super S, ? extends P> getter) {
		if (type.equals(Boolean.TYPE)) {
			return new BooleanFieldMapper<S, T>(
					ObjectGetterFactory.<S, P>toBooleanGetter(getter),
					ObjectSetterFactory.<T, P>toBooleanSetter(setter));
		} else if (type.equals(Integer.TYPE)) {
			return new IntFieldMapper<S, T>(
					ObjectGetterFactory.<S, P>toIntGetter(getter),
					ObjectSetterFactory.<T, P>toIntSetter(setter));
		} else if (type.equals(Long.TYPE)) {
			return new LongFieldMapper<S, T>(
					ObjectGetterFactory.<S, P>toLongGetter(getter),
					ObjectSetterFactory.<T, P>toLongSetter(setter));
		} else if (type.equals(Float.TYPE)) {
			return new FloatFieldMapper<S, T>(
					ObjectGetterFactory.<S, P>toFloatGetter(getter),
					ObjectSetterFactory.<T, P>toFloatSetter(setter));
		} else if (type.equals(Double.TYPE)) {
			return new DoubleFieldMapper<S, T>(
					ObjectGetterFactory.<S, P>toDoubleGetter(getter),
					ObjectSetterFactory.<T, P>toDoubleSetter(setter));
		} else if (type.equals(Byte.TYPE)) {
			return new ByteFieldMapper<S, T>(
					ObjectGetterFactory.<S, P>toByteGetter(getter),
					ObjectSetterFactory.<T, P>toByteSetter(setter));
		} else if (type.equals(Character.TYPE)) {
			return new CharacterFieldMapper<S, T>(
					ObjectGetterFactory.<S, P>toCharGetter(getter),
					ObjectSetterFactory.<T, P>toCharacterSetter(setter));
		} else if (type.equals(Short.TYPE)) {
			return new ShortFieldMapper<S, T>(
					ObjectGetterFactory.<S, P>toShortGetter(getter),
					ObjectSetterFactory.<T, P>toShortSetter(setter));
		} else {
			throw new UnsupportedOperationException("Type " + type
					+ " is not primitive");
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T, P> FieldMapper<S, T> newFieldMapper(PropertyMapping<T, P, K,
			FieldMapperColumnDefinition<K>> propertyMapping,
												   MappingContextFactoryBuilder contextFactoryBuilder,
												   MapperBuilderErrorHandler mappingErrorHandler) {

		final PropertyMeta<T, P> propertyMeta = propertyMapping.getPropertyMeta();
		final Type propertyType = propertyMeta.getPropertyType();
		final Setter<T, ? super P> setter = propertyMeta.getSetter();
		final K key = propertyMapping.getColumnKey();
		final Class<P> type = TypeHelper.toClass(propertyType);

        @SuppressWarnings("unchecked")
		Getter<? super S, ? extends P> getter = (Getter<? super S, ? extends P>) propertyMapping.getColumnDefinition().getCustomGetter();


        GetterFactory<? super S, K> getterFactory = this.getterFactory;

        if (propertyMapping.getColumnDefinition().hasCustomFactory()) {
            getterFactory = (GetterFactory<? super S, K>) propertyMapping.getColumnDefinition().getCustomGetterFactory();
        }

		if (getter == null) {
			getter = getterFactory.newGetter(propertyType, key, propertyMapping.getColumnDefinition());
		}
		if (getter == null) {
			final ClassMeta<P> classMeta = propertyMeta.getPropertyClassMeta();


			InstantiatorDefinition.CompatibilityScorer scorer = getCompatibilityScorer(key);
			InstantiatorDefinition id = InstantiatorDefinition.lookForCompatibleOneArgument(classMeta.getInstantiatorDefinitions(),
					scorer);

			if (id != null) {
				final Type sourceType = id.getParameters()[0].getGenericType();
				getter = getterFactory.newGetter(sourceType, key, null);
				if (getter != null) {
					Instantiator instantiator =
							classMeta.getReflectionService().getInstantiatorFactory().getOneArgIdentityInstantiator(id);
					getter = new InstantiatorOnGetter(instantiator, getter);
				}
			}
		}
		if (getter == null) {
			mappingErrorHandler.getterNotFound("Could not find getter for " + key + " type " + propertyType);
			return null;
		} else {
			if (type.isPrimitive() ) {
				return this.<T, P>primitiveIndexedFieldMapper(type, setter, getter);
			}

			return new FieldMapperImpl<S, T, P>(getter, setter);
		}
	}

	private InstantiatorDefinition.CompatibilityScorer getCompatibilityScorer(K key) {
		if (key instanceof TypeAffinity) {
			TypeAffinity ta = (TypeAffinity) key;
			Class<?>[] affinities = ta.getAffinities();

			if (affinities != null && affinities.length > 0) {
				return new TypeAffinityCompatibilityScorer(affinities);
			}
		}
		return new DefaultCompatibilityScorer();
	}

	private static class DefaultCompatibilityScorer implements InstantiatorDefinition.CompatibilityScorer {
		@Override
        public int score(InstantiatorDefinition id) {
			Package aPackage = id.getParameters()[0].getType().getPackage();
			if (aPackage != null && aPackage.getName().equals("java.lang")) {
				return 1;
			}
            return 0;
        }
	}

	private static class TypeAffinityCompatibilityScorer implements InstantiatorDefinition.CompatibilityScorer {
		private final Class<?>[] classes;

		private TypeAffinityCompatibilityScorer(Class<?>[] classes) {
			this.classes = classes;
		}

		@Override
		public int score(InstantiatorDefinition id) {
			Class<?> paramType = TypeHelper.toBoxedClass(id.getParameters()[0].getType());

			for(int i = 0; i < classes.length; i++) {
				Class<?> c = classes[i];
				if (c.isAssignableFrom(paramType)) {
					return classes.length - i + 10;
				}
			}

			Package aPackage = paramType.getPackage();
			if (aPackage != null && aPackage.getName().equals("java.lang")) {
				return 1;
			}
			return 0;
		}
	}
}
