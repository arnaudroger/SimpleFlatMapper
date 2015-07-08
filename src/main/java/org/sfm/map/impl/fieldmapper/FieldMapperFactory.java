package org.sfm.map.impl.fieldmapper;

import org.sfm.map.*;
import org.sfm.map.impl.FieldMapperColumnDefinition;
import org.sfm.map.impl.PropertyMapping;
import org.sfm.reflect.*;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.PropertyMeta;

import java.lang.reflect.Type;

public final class FieldMapperFactory<S, K extends FieldKey<K>>  {

	private final GetterFactory<? super S, K> getterFactory;

	public FieldMapperFactory(GetterFactory<? super S, K> getterFactory) {
		this.getterFactory = getterFactory;
	}


	private <T, P> FieldMapper<S, T> primitiveIndexedFieldMapper(final Class<P> type, final Setter<T, ? super P> setter, final Getter<? super S, ? extends P> getter) {
		if (type.equals(Boolean.TYPE)) {
			return new BooleanFieldMapper<S, T>(
					ObjectGetterFactory.toBooleanGetter(getter),
					ObjectSetterFactory.toBooleanSetter(setter));
		} else if (type.equals(Integer.TYPE)) {
			return new IntFieldMapper<S, T>(
					ObjectGetterFactory.toIntGetter(getter),
					ObjectSetterFactory.toIntSetter(setter));
		} else if (type.equals(Long.TYPE)) {
			return new LongFieldMapper<S, T>(
					ObjectGetterFactory.toLongGetter(getter),
					ObjectSetterFactory.toLongSetter(setter));
		} else if (type.equals(Float.TYPE)) {
			return new FloatFieldMapper<S, T>(
					ObjectGetterFactory.toFloatGetter(getter),
					ObjectSetterFactory.toFloatSetter(setter));
		} else if (type.equals(Double.TYPE)) {
			return new DoubleFieldMapper<S, T>(
					ObjectGetterFactory.toDoubleGetter(getter),
					ObjectSetterFactory.toDoubleSetter(setter));
		} else if (type.equals(Byte.TYPE)) {
			return new ByteFieldMapper<S, T>(
					ObjectGetterFactory.toByteGetter(getter),
					ObjectSetterFactory.toByteSetter(setter));
		} else if (type.equals(Character.TYPE)) {
			return new CharacterFieldMapper<S, T>(
					ObjectGetterFactory.toCharGetter(getter),
					ObjectSetterFactory.toCharacterSetter(setter));
		} else if (type.equals(Short.TYPE)) {
			return new ShortFieldMapper<S, T>(
					ObjectGetterFactory.toShortGetter(getter),
					ObjectSetterFactory.toShortSetter(setter));
		} else {
			throw new UnsupportedOperationException("Type " + type
					+ " is not primitive");
		}
	}

	@SuppressWarnings("unchecked")
	public <T, P> FieldMapper<S, T> newFieldMapper(PropertyMapping<T, P, K ,
                            FieldMapperColumnDefinition<K, S>> propertyMapping,
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
            getterFactory = propertyMapping.getColumnDefinition().getCustomGetterFactory();
        }

		if (getter == null) {
			getter = getterFactory.newGetter(propertyType, key, propertyMapping.getColumnDefinition());
		}
		if (getter == null) {
			final ClassMeta<P> classMeta = propertyMeta.getPropertyClassMeta();
			for(InstantiatorDefinition id : classMeta.getInstantiatorDefinitions()) {
				if (id.getParameters().length == 1) {
					final Type sourceType = id.getParameters()[0].getGenericType();
					getter = getterFactory.newGetter(sourceType, key, null);
					if (getter != null) {
						Instantiator instantiator =
								classMeta.getReflectionService().getInstantiatorFactory().getOneArgIdentityInstantiator(id);
						getter = new InstantiatorOnGetter(instantiator, getter);
						break;
					}
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
}
