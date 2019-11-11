package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.converter.ContextualConverter;
import org.simpleflatmapper.converter.ConverterService;
import org.simpleflatmapper.map.getter.BooleanContextualGetter;
import org.simpleflatmapper.map.getter.BoxedBooleanContextualGetter;
import org.simpleflatmapper.map.getter.BoxedByteContextualGetter;
import org.simpleflatmapper.map.getter.BoxedCharacterContextualGetter;
import org.simpleflatmapper.map.getter.BoxedDoubleContextualGetter;
import org.simpleflatmapper.map.getter.BoxedFloatContextualGetter;
import org.simpleflatmapper.map.getter.BoxedIntContextualGetter;
import org.simpleflatmapper.map.getter.BoxedLongContextualGetter;
import org.simpleflatmapper.map.getter.BoxedShortContextualGetter;
import org.simpleflatmapper.map.getter.ByteContextualGetter;
import org.simpleflatmapper.map.getter.CharacterContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.DoubleContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetterFactory;
import org.simpleflatmapper.map.getter.ContextualGetterWithDefaultValue;
import org.simpleflatmapper.map.getter.FloatContextualGetter;
import org.simpleflatmapper.map.getter.InstantiatorContextualGetter;
import org.simpleflatmapper.map.getter.IntContextualGetter;
import org.simpleflatmapper.map.getter.LongContextualGetter;
import org.simpleflatmapper.map.getter.ShortContextualGetter;
import org.simpleflatmapper.map.impl.JoinUtils;
import org.simpleflatmapper.map.mapper.ColumnDefinition;
import org.simpleflatmapper.map.mapper.ConstantSourceMapperBuilder;
import org.simpleflatmapper.map.getter.ContextualGetterAdapter;
import org.simpleflatmapper.map.mapper.DefaultConstantSourceMapperBuilder;
import org.simpleflatmapper.map.property.ConverterProperty;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.map.property.DefaultValueProperty;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
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
import java.util.List;

public final class ConstantSourceFieldMapperFactoryImpl<S, K extends FieldKey<K>> implements ConstantSourceFieldMapperFactory<S,K> {

	private final ContextualGetterFactory<? super S, K> getterFactory;
	private final ConverterService converterService;
	private final Type sourceType;


	public ConstantSourceFieldMapperFactoryImpl(ContextualGetterFactory<? super S, K> getterFactory, ConverterService converterService, Type sourceType) {
		this.getterFactory = getterFactory;
		this.converterService = converterService;
		this.sourceType = sourceType;
	}


	@SuppressWarnings("unchecked")
	private <T, P> FieldMapper<S, T> primitiveIndexedFieldMapper(final Class<P> type, final Setter<? super T, ? super P> setter, final ContextualGetter<? super S, ? extends P> getter) {
		if (type.equals(Boolean.TYPE)) {
			return new BooleanConstantSourceFieldMapper<S, T>(
					toBooleanGetter((ContextualGetter<S, ? extends Boolean>) getter),
					ObjectSetterFactory.<T>toBooleanSetter((Setter<T, ? super Boolean>) setter));
		} else if (type.equals(Integer.TYPE)) {
			return new IntConstantSourceFieldMapper<S, T>(
					toIntGetter((ContextualGetter<S, ? extends Integer>) getter),
					ObjectSetterFactory.<T>toIntSetter((Setter<T, ? super Integer>) setter));
		} else if (type.equals(Long.TYPE)) {
			return new LongConstantSourceFieldMapper<S, T>(
					toLongGetter((ContextualGetter<S, ? extends Long>) getter),
					ObjectSetterFactory.<T>toLongSetter((Setter<T, ? super Long>) setter));
		} else if (type.equals(Float.TYPE)) {
			return new FloatConstantSourceFieldMapper<S, T>(
					toFloatGetter((ContextualGetter<S, ? extends Float>) getter),
					ObjectSetterFactory.<T>toFloatSetter((Setter<T, ? super Float>) setter));
		} else if (type.equals(Double.TYPE)) {
			return new DoubleConstantSourceFieldMapper<S, T>(
					toDoubleGetter((ContextualGetter<S, ? extends Double>) getter),
					ObjectSetterFactory.<T>toDoubleSetter((Setter<T, ? super Double>) setter));
		} else if (type.equals(Byte.TYPE)) {
			return new ByteConstantSourceFieldMapper<S, T>(
					toByteGetter((ContextualGetter<S, ? extends Byte>) getter),
					ObjectSetterFactory.<T>toByteSetter((Setter<T, ? super Byte>) setter));
		} else if (type.equals(Character.TYPE)) {
			return new CharacterConstantSourceFieldMapper<S, T>(
					toCharGetter((ContextualGetter<S, ? extends Character>) getter),
					ObjectSetterFactory.<T>toCharacterSetter((Setter<T, ? super Character>) setter));
		} else if (type.equals(Short.TYPE)) {
			return new ShortConstantSourceFieldMapper<S, T>(
					toShortGetter((ContextualGetter<S, ? extends Short>) getter),
					ObjectSetterFactory.<T>toShortSetter((Setter<T, ? super Short>) setter));
		} else {
			throw new UnsupportedOperationException("Type " + type
					+ " is not primitive");
		}
	}


	@SuppressWarnings("unchecked")
	public static <T> BooleanContextualGetter<T> toBooleanGetter(final ContextualGetter<T, ? extends Boolean> getter) {
		if (getter instanceof BooleanContextualGetter) {
			return (BooleanContextualGetter<T>) getter;
		} else {
			return new BoxedBooleanContextualGetter<T>(getter);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> IntContextualGetter<T> toIntGetter(ContextualGetter<T, ? extends Integer> getter) {
		if (getter instanceof IntContextualGetter) {
			return (IntContextualGetter<T>) getter;
		} else {
			return new BoxedIntContextualGetter<T>(getter);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> LongContextualGetter<T> toLongGetter(ContextualGetter<T, ? extends Long> getter) {
		if (getter instanceof LongContextualGetter) {
			return (LongContextualGetter<T>) getter;
		} else {
			return new BoxedLongContextualGetter<T>(getter);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> FloatContextualGetter<T> toFloatGetter(ContextualGetter<T, ? extends Float> getter) {
		if (getter instanceof FloatContextualGetter) {
			return (FloatContextualGetter<T>) getter;
		} else {
			return new BoxedFloatContextualGetter<T>(getter);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> DoubleContextualGetter<T> toDoubleGetter(ContextualGetter<T, ? extends Double> getter) {
		if (getter instanceof DoubleContextualGetter) {
			return (DoubleContextualGetter<T>) getter;
		} else {
			return new BoxedDoubleContextualGetter<T>(getter);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> ByteContextualGetter<T> toByteGetter(ContextualGetter<T, ? extends Byte> getter) {
		if (getter instanceof ByteContextualGetter) {
			return (ByteContextualGetter<T>) getter;
		} else {
			return new BoxedByteContextualGetter<T>(getter);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> ShortContextualGetter<T> toShortGetter(ContextualGetter<T, ? extends Short> getter) {
		if (getter instanceof ShortContextualGetter) {
			return (ShortContextualGetter<T>) getter;
		} else {
			return new BoxedShortContextualGetter<T>(getter);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> CharacterContextualGetter<T> toCharGetter(ContextualGetter<T, ? extends Character> getter) {
		if (getter instanceof CharacterContextualGetter) {
			return (CharacterContextualGetter<T>) getter;
		} else {
			return new BoxedCharacterContextualGetter<T>(getter);
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

		ContextualGetter<? super S, ? extends P> getter = getGetterFromSource(key,
				propertyMapping.getPropertyMeta().getPropertyType(),
				propertyMapping.getColumnDefinition(), 
				propertyMeta.getPropertyClassMetaSupplier(), contextFactoryBuilder);

		if (getter == null) {
			
			mappingErrorHandler.accessorNotFound(DefaultConstantSourceMapperBuilder.getterNotFoundErrorMessage(propertyMapping));
			return null;
		} else {
			if (type.isPrimitive() ) {
				return this.<T, P>primitiveIndexedFieldMapper(type, setter, getter);
			}
			
			if (propertyMapping.getColumnDefinition().isInferNull()
					&& JoinUtils.isArrayElement(propertyMapping.getPropertyMeta())) {
				return new ConstantSourceFieldMapper<S, T, P>(getter, new NullValueFilterSetter(setter));
			} else {
				return new ConstantSourceFieldMapper<S, T, P>(getter, setter);
			}
		}
	}

	@Override
	public <P> ContextualGetter<? super S, ? extends P> getGetterFromSource(K columnKey, Type propertyType, ColumnDefinition<K, ?> columnDefinition, Supplier<ClassMeta<P>> propertyClassMetaSupplier, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder) {
		@SuppressWarnings("unchecked")
		ContextualGetter<? super S, ? extends P> getter = ContextualGetterAdapter.of((Getter<? super S, ? extends P>) columnDefinition.getCustomGetterFrom(sourceType));

		if (getter == null) {
			ContextualGetterFactory<? super S, K> customGetterFactory = (ContextualGetterFactory<? super S, K>) columnDefinition.getCustomGetterFactoryFrom(sourceType);
			if (customGetterFactory != null) {
				getter = (ContextualGetter<? super S, ? extends P>) customGetterFactory.<P>newGetter(propertyType, columnKey, mappingContextFactoryBuilder, columnDefinition.properties());
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
					getter = new ContextualGetterWithDefaultValue<S, P>(getter, (P) value);
				} else {
					throw new IllegalArgumentException("Incompatible default value " + value + " type " + value.getClass() + " with property " + columnKey + " of type " + propertyType);
				}
			}
		}

		return getter;
	}

	private <P, J> ContextualGetter<? super S, ? extends P> lookForAlternativeGetter(ClassMeta<P> classMeta, K key, ColumnDefinition<K, ?> columnDefinition, Collection<Type> types, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder) {
		// look for converter
		Type propertyType = classMeta.getType();
		Type sourceType = key.getType(propertyType);
		Object[] properties = columnDefinition.properties();
		ContextualConverter<? super J, ? extends P> converter = converterService.findConverter(sourceType, propertyType, mappingContextFactoryBuilder, properties);

		if (converter != null) {
			ContextualGetter<? super S, ? extends J> getter = getterFactory.newGetter(sourceType, key, mappingContextFactoryBuilder, properties);

			return new FieldMapperGetterWithConverter<S, J, P>(converter, getter);
		}

		return lookForInstantiatorGetter(classMeta, key, columnDefinition, types, mappingContextFactoryBuilder);
	}

	public <P> ContextualGetter<? super S, ? extends P> lookForInstantiatorGetter(ClassMeta<P> classMeta, K key, ColumnDefinition<K, ?> columnDefinition, Collection<Type> types, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder) {


		InstantiatorDefinitions.CompatibilityScorer scorer = InstantiatorDefinitions.getCompatibilityScorer(key);
		List<InstantiatorDefinition> instantiatorDefinitions = classMeta.getInstantiatorDefinitions();
		InstantiatorDefinition id = InstantiatorDefinitions.lookForCompatibleOneArgument(instantiatorDefinitions, scorer);

		if (id != null) {
            return getGetterInstantiator(classMeta, id, key, columnDefinition, types, mappingContextFactoryBuilder);
        }
		return null;
	}

	private <T, P> ContextualGetter<? super S, ? extends P> getGetterInstantiator(
			ClassMeta<P> classMeta,
			InstantiatorDefinition id, K key, ColumnDefinition<K, ?> columnDefinition,
			Collection<Type> types,
			MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder) {

		Instantiator<? super T, ? extends P> instantiator =
				classMeta.getReflectionService().getInstantiatorFactory().getOneArgIdentityInstantiator(id, classMeta.getReflectionService().builderIgnoresNullValues());

		final Type paramType = id.getParameters()[0].getGenericType();

		ContextualGetter<? super S, ? extends T> subGetter = getterFactory.newGetter(paramType, key, mappingContextFactoryBuilder, columnDefinition );

		if (subGetter == null) {
			if (types.contains(paramType)) {
				// loop circuit cutter
				return null;
			}
			types.add(paramType);
			// converter?
			Type sourceType = key.getType(paramType);
			ContextualConverter converter = converterService.findConverter(sourceType, paramType, mappingContextFactoryBuilder, columnDefinition.properties());
			
			if (converter != null) {
				ContextualGetter sourceTypeGetter = getterFactory.newGetter(sourceType, key, mappingContextFactoryBuilder, columnDefinition);
				subGetter = new FieldMapperGetterWithConverter(converter, sourceTypeGetter);
			} else {
				subGetter = lookForInstantiatorGetter(classMeta.getReflectionService().<T>getClassMeta(paramType), key, columnDefinition, types, mappingContextFactoryBuilder);
			}
		}

		if (subGetter != null) {
			return new InstantiatorContextualGetter<T, S, P>(instantiator, subGetter);
		} else return null;
	}


}
