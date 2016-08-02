package org.simpleflatmapper.core.map.fieldmapper;

import org.simpleflatmapper.core.map.*;
import org.simpleflatmapper.core.map.column.FieldMapperColumnDefinition;
import org.simpleflatmapper.core.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.core.map.mapper.PropertyMapping;
import org.simpleflatmapper.core.reflect.*;
import org.simpleflatmapper.core.reflect.meta.ClassMeta;
import org.simpleflatmapper.core.reflect.meta.PropertyMeta;
import org.simpleflatmapper.core.utils.ErrorDoc;

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

		if (getter == null) {
			getter = getterFromFactory(propertyMapping, propertyType);
		}

		if (getter == null) {
			mappingErrorHandler.accessorNotFound("Could not find getter for " + key + " type " + propertyType
					+ " See " + ErrorDoc.toUrl("CSFM_GETTER_NOT_FOUND"));
			return null;
		} else {
			if (type.isPrimitive() ) {
				return this.<T, P>primitiveIndexedFieldMapper(type, setter, getter);
			}

			return new FieldMapperImpl<S, T, P>(getter, setter);
		}
	}

	private <T, P> Getter<? super S, ? extends P> getterFromFactory(PropertyMapping<T, P, K, FieldMapperColumnDefinition<K>> propertyMapping, Type propertyType) {
		Getter<? super S, ? extends P> getter = null;
		if (propertyMapping.getColumnDefinition().hasCustomFactory()) {
            GetterFactory<? super S, K> cGetterFactory = (GetterFactory<? super S, K>) propertyMapping.getColumnDefinition().getCustomGetterFactory();
            getter = cGetterFactory.newGetter(propertyType, propertyMapping.getColumnKey(), propertyMapping.getColumnDefinition());
        }

		if (getter == null) {
            getter = getterFactory.newGetter(propertyType, propertyMapping.getColumnKey(), propertyMapping.getColumnDefinition());
        }

        // try to identify constructor that we could build from
		if (getter == null) {
			final ClassMeta<P> classMeta = propertyMapping.getPropertyMeta().getPropertyClassMeta();

			InstantiatorDefinitions.CompatibilityScorer scorer = InstantiatorDefinitions.getCompatibilityScorer(propertyMapping.getColumnKey());
			InstantiatorDefinition id = InstantiatorDefinitions.lookForCompatibleOneArgument(classMeta.getInstantiatorDefinitions(),
					scorer);

			if (id != null) {
				final Type sourceType = id.getParameters()[0].getGenericType();
				getter = getterFactory.newGetter(sourceType, propertyMapping.getColumnKey(), propertyMapping.getColumnDefinition());
				if (getter != null) {
					Instantiator instantiator =
							classMeta.getReflectionService().getInstantiatorFactory().getOneArgIdentityInstantiator(id);
					getter = new InstantiatorOnGetter(instantiator, getter);
				}
			}
		}

		return getter;
	}


}
