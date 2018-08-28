package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.converter.ConverterService;
import org.simpleflatmapper.map.impl.JoinUtils;
import org.simpleflatmapper.map.mapper.ColumnDefinition;
import org.simpleflatmapper.map.mapper.ConstantSourceMapperBuilder;
import org.simpleflatmapper.map.mapper.FieldMapperGetterAdapter;
import org.simpleflatmapper.map.property.ConverterProperty;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.map.property.DefaultValueProperty;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.map.impl.FieldMapperGetterWithConverter;
import org.simpleflatmapper.reflect.instantiator.InstantiatorDefinitions;
import org.simpleflatmapper.reflect.ObjectSetterFactory;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MapperBuilderErrorHandler;
import org.simpleflatmapper.util.Supplier;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;

public final class ConstantSourceFieldMapperFactoryImpl<S, K extends FieldKey<K>> implements ConstantSourceFieldMapperFactory<S,K> {

	private final FieldMapperGetterFactory<? super S, K> getterFactory;
	private final ConverterService converterService;
	private final Type sourceType;


	public ConstantSourceFieldMapperFactoryImpl(FieldMapperGetterFactory<? super S, K> getterFactory, ConverterService converterService, Type sourceType) {
		this.getterFactory = getterFactory;
		this.converterService = converterService;
		this.sourceType = sourceType;
	}


	@SuppressWarnings("unchecked")
	private <T, P> FieldMapper<S, T> primitiveIndexedFieldMapper(final Class<P> type, final Setter<? super T, ? super P> setter, final FieldMapperGetter<? super S, ? extends P> getter) {
		if (type.equals(Boolean.TYPE)) {
			return new BooleanFieldMapper<S, T>(
					toBooleanGetter((FieldMapperGetter<S, ? extends Boolean>) getter),
					ObjectSetterFactory.<T>toBooleanSetter((Setter<T, ? super Boolean>) setter));
		} else if (type.equals(Integer.TYPE)) {
			return new IntFieldMapper<S, T>(
					toIntGetter((FieldMapperGetter<S, ? extends Integer>) getter),
					ObjectSetterFactory.<T>toIntSetter((Setter<T, ? super Integer>) setter));
		} else if (type.equals(Long.TYPE)) {
			return new LongFieldMapper<S, T>(
					toLongGetter((FieldMapperGetter<S, ? extends Long>) getter),
					ObjectSetterFactory.<T>toLongSetter((Setter<T, ? super Long>) setter));
		} else if (type.equals(Float.TYPE)) {
			return new FloatFieldMapper<S, T>(
					toFloatGetter((FieldMapperGetter<S, ? extends Float>) getter),
					ObjectSetterFactory.<T>toFloatSetter((Setter<T, ? super Float>) setter));
		} else if (type.equals(Double.TYPE)) {
			return new DoubleFieldMapper<S, T>(
					toDoubleGetter((FieldMapperGetter<S, ? extends Double>) getter),
					ObjectSetterFactory.<T>toDoubleSetter((Setter<T, ? super Double>) setter));
		} else if (type.equals(Byte.TYPE)) {
			return new ByteFieldMapper<S, T>(
					toByteGetter((FieldMapperGetter<S, ? extends Byte>) getter),
					ObjectSetterFactory.<T>toByteSetter((Setter<T, ? super Byte>) setter));
		} else if (type.equals(Character.TYPE)) {
			return new CharacterFieldMapper<S, T>(
					toCharGetter((FieldMapperGetter<S, ? extends Character>) getter),
					ObjectSetterFactory.<T>toCharacterSetter((Setter<T, ? super Character>) setter));
		} else if (type.equals(Short.TYPE)) {
			return new ShortFieldMapper<S, T>(
					toShortGetter((FieldMapperGetter<S, ? extends Short>) getter),
					ObjectSetterFactory.<T>toShortSetter((Setter<T, ? super Short>) setter));
		} else {
			throw new UnsupportedOperationException("Type " + type
					+ " is not primitive");
		}
	}


	@SuppressWarnings("unchecked")
	public static <T> BooleanFieldMapperGetter<T> toBooleanGetter(final FieldMapperGetter<T, ? extends Boolean> getter) {
		if (getter instanceof BooleanFieldMapperGetter) {
			return (BooleanFieldMapperGetter<T>) getter;
		} else {
			return new BoxedBooleanFieldMapperGetter<T>(getter);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> IntFieldMapperGetter<T> toIntGetter(FieldMapperGetter<T, ? extends Integer> getter) {
		if (getter instanceof IntFieldMapperGetter) {
			return (IntFieldMapperGetter<T>) getter;
		} else {
			return new BoxedIntFieldMapperGetter<T>(getter);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> LongFieldMapperGetter<T> toLongGetter(FieldMapperGetter<T, ? extends Long> getter) {
		if (getter instanceof LongFieldMapperGetter) {
			return (LongFieldMapperGetter<T>) getter;
		} else {
			return new BoxedLongFieldMapperGetter<T>(getter);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> FloatFieldMapperGetter<T> toFloatGetter(FieldMapperGetter<T, ? extends Float> getter) {
		if (getter instanceof FloatFieldMapperGetter) {
			return (FloatFieldMapperGetter<T>) getter;
		} else {
			return new BoxedFloatFieldMapperGetter<T>(getter);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> DoubleFieldMapperGetter<T> toDoubleGetter(FieldMapperGetter<T, ? extends Double> getter) {
		if (getter instanceof DoubleFieldMapperGetter) {
			return (DoubleFieldMapperGetter<T>) getter;
		} else {
			return new BoxedDoubleFieldMapperGetter<T>(getter);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> ByteFieldMapperGetter<T> toByteGetter(FieldMapperGetter<T, ? extends Byte> getter) {
		if (getter instanceof ByteFieldMapperGetter) {
			return (ByteFieldMapperGetter<T>) getter;
		} else {
			return new BoxedByteFieldMapperGetter<T>(getter);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> ShortFieldMapperGetter<T> toShortGetter(FieldMapperGetter<T, ? extends Short> getter) {
		if (getter instanceof ShortFieldMapperGetter) {
			return (ShortFieldMapperGetter<T>) getter;
		} else {
			return new BoxedShortFieldMapperGetter<T>(getter);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> CharacterFieldMapperGetter<T> toCharGetter(FieldMapperGetter<T, ? extends Character> getter) {
		if (getter instanceof CharacterFieldMapperGetter) {
			return (CharacterFieldMapperGetter<T>) getter;
		} else {
			return new BoxedCharacterFieldMapperGetter<T>(getter);
		}
	}
	

	@Override
	@SuppressWarnings("unchecked")
	public <T, P> FieldMapper<S, T> newFieldMapper(PropertyMapping<T, P, K> propertyMapping,
												   MappingContextFactoryBuilder contextFactoryBuilder,
												   MapperBuilderErrorHandler mappingErrorHandler
												   ) {

		final PropertyMeta<T, P> propertyMeta = propertyMapping.getPropertyMeta();
		final Type propertyType = propertyMeta.getPropertyType();
		final Setter<? super T, ? super P> setter = propertyMeta.getSetter();
		final K key = propertyMapping.getColumnKey();
		final Class<P> type = TypeHelper.toClass(propertyType);

		FieldMapperGetter<? super S, ? extends P> getter = getGetterFromSource(key,
				propertyMapping.getPropertyMeta().getPropertyType(),
				propertyMapping.getColumnDefinition(), 
				propertyMeta.getPropertyClassMetaSupplier(), contextFactoryBuilder);

		if (getter == null) {
			
			mappingErrorHandler.accessorNotFound(ConstantSourceMapperBuilder.getterNotFoundErrorMessage(propertyMapping));
			return null;
		} else {
			if (type.isPrimitive() ) {
				return this.<T, P>primitiveIndexedFieldMapper(type, setter, getter);
			}
			
			if (propertyMapping.getColumnDefinition().isKey() 
					&& JoinUtils.isArrayElement(propertyMapping.getPropertyMeta())) {
				return new FieldMapperImpl<S, T, P>(getter, new NullValueFilterSetter(setter));
			} else {
				return new FieldMapperImpl<S, T, P>(getter, setter);
			}
		}
	}

	@Override
	public <P> FieldMapperGetter<? super S, ? extends P> getGetterFromSource(K columnKey, Type propertyType, ColumnDefinition<K, ?> columnDefinition, Supplier<ClassMeta<P>> propertyClassMetaSupplier, MappingContextFactoryBuilder<?, K> mappingContextFactoryBuilder) {
		@SuppressWarnings("unchecked")
		FieldMapperGetter<? super S, ? extends P> getter = FieldMapperGetterAdapter.of((Getter<? super S, ? extends P>) columnDefinition.getCustomGetterFrom(sourceType));

		if (getter == null) {
            GetterFactory<? super S, K> customGetterFactory = (GetterFactory<? super S, K>) columnDefinition.getCustomGetterFactoryFrom(sourceType);
			if (customGetterFactory != null) {
				getter = FieldMapperGetterAdapter.of(customGetterFactory.newGetter(propertyType, columnKey, columnDefinition.properties()));
			}
        }

		ConverterProperty converterProperty = columnDefinition.lookFor(ConverterProperty.class);
		
		if (converterProperty != null) {
			Type t = converterProperty.inType;
			if (Object.class.equals(t)) { // lost type info... assume sql type is right
				t = columnKey.getType(t);
			}
			getter = getterFactory.<P>newGetter(t, columnKey, mappingContextFactoryBuilder, columnDefinition.properties());
			if (getter == null) {
				return null;
			}
			return new FieldMapperGetterWithConverter(converterProperty.function, getter);
		}

		if (getter == null) {
            getter = getterFactory.newGetter(propertyType, columnKey, mappingContextFactoryBuilder, columnDefinition.properties());
        }
		// try to identify constructor that we could build from
		if (getter == null) {
			getter = lookForAlternativeGetter(propertyClassMetaSupplier.get(), columnKey, columnDefinition, new HashSet<Type>(), mappingContextFactoryBuilder);
		}

		DefaultValueProperty defaultValueProperty = columnDefinition.lookFor(DefaultValueProperty.class);
		if (defaultValueProperty != null) {
			Object value = defaultValueProperty.getValue();
			if (value != null) {
				if (TypeHelper.isAssignable(propertyType, value.getClass())) {
					getter = new FieldMapperGetterWithDefaultValue<S, P>(getter, (P) value);
				} else {
					throw new IllegalArgumentException("Incompatible default value " + value + " type " + value.getClass() + " with property " + columnKey + " of type " + propertyType);
				}
			}
		}

		return getter;
	}

	private <P, J> FieldMapperGetter<? super S, ? extends P> lookForAlternativeGetter(ClassMeta<P> classMeta, K key, ColumnDefinition<K, ?> columnDefinition, Collection<Type> types, MappingContextFactoryBuilder<?, K> mappingContextFactoryBuilder) {
		// look for converter
		Type propertyType = classMeta.getType();
		Type sourceType = key.getType(propertyType);
		Object[] properties = columnDefinition.properties();
		Converter<? super J, ? extends P> converter = converterService.findConverter(sourceType, propertyType, properties);

		if (converter != null) {
			FieldMapperGetter<? super S, ? extends J> getter = getterFactory.newGetter(sourceType, key, mappingContextFactoryBuilder, properties);

			return new FieldMapperGetterWithConverter<S, J, P>(converter, getter);
		}

		return lookForInstantiatorGetter(classMeta, key, columnDefinition, types, mappingContextFactoryBuilder);
	}

	public <P> FieldMapperGetter<? super S, ? extends P> lookForInstantiatorGetter(ClassMeta<P> classMeta, K key, ColumnDefinition<K, ?> columnDefinition, Collection<Type> types, MappingContextFactoryBuilder<?, K> mappingContextFactoryBuilder) {


		InstantiatorDefinitions.CompatibilityScorer scorer = InstantiatorDefinitions.getCompatibilityScorer(key);
		InstantiatorDefinition id = InstantiatorDefinitions.lookForCompatibleOneArgument(classMeta.getInstantiatorDefinitions(),
                scorer);

		if (id != null) {
            return getGetterInstantiator(classMeta, id, key, columnDefinition, types, mappingContextFactoryBuilder);
        }
		return null;
	}

	private <T, P> FieldMapperGetter<? super S, ? extends P> getGetterInstantiator(
			ClassMeta<P> classMeta,
			InstantiatorDefinition id, K key, ColumnDefinition<K, ?> columnDefinition,
			Collection<Type> types,
			MappingContextFactoryBuilder<?, K> mappingContextFactoryBuilder) {

		Instantiator<? super T, ? extends P> instantiator =
				classMeta.getReflectionService().getInstantiatorFactory().getOneArgIdentityInstantiator(id, classMeta.getReflectionService().builderIgnoresNullValues());

		final Type paramType = id.getParameters()[0].getGenericType();

		FieldMapperGetter<? super S, ? extends T> subGetter = getterFactory.newGetter(paramType, key, mappingContextFactoryBuilder, columnDefinition );

		if (subGetter == null) {
			if (types.contains(paramType)) {
				// loop circuit cutter
				return null;
			}
			types.add(paramType);
			// converter?
			Type sourceType = key.getType(paramType);
			Converter converter = converterService.findConverter(sourceType, paramType, columnDefinition.properties());
			
			if (converter != null) {
				FieldMapperGetter sourceTypeGetter = getterFactory.newGetter(sourceType, key, mappingContextFactoryBuilder, columnDefinition);
				subGetter = new FieldMapperGetterWithConverter(converter, sourceTypeGetter);
			} else {
				subGetter = lookForInstantiatorGetter(classMeta.getReflectionService().<T>getClassMeta(paramType), key, columnDefinition, types, mappingContextFactoryBuilder);
			}
		}

		if (subGetter != null) {
			return new InstantiatorFieldMapperGetter<T, S, P>(instantiator, subGetter);
		} else return null;
	}


}
