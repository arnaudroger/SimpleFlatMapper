package org.simpleflatmapper.csv.impl;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.CellValueReaderFactory;
import org.simpleflatmapper.csv.CsvColumnDefinition;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.csv.impl.cellreader.*;
import org.simpleflatmapper.csv.impl.primitive.*;
import org.simpleflatmapper.csv.ParsingContextFactoryBuilder;
import org.simpleflatmapper.csv.mapper.CellSetter;
import org.simpleflatmapper.csv.mapper.CsvMapperCellHandler;
import org.simpleflatmapper.csv.mapper.DelayedCellSetterFactory;
import org.simpleflatmapper.map.MapperBuilderErrorHandler;
import org.simpleflatmapper.map.property.DefaultValueProperty;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.instantiator.InstantiatorDefinitions;
import org.simpleflatmapper.reflect.meta.SubPropertyMeta;
import org.simpleflatmapper.reflect.setter.NullSetter;
import org.simpleflatmapper.reflect.ObjectSetterFactory;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.util.ErrorDoc;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.util.Date;

public final class CellSetterFactory {


	public static final InstantiatorDefinitions.CompatibilityScorer COMPATIBILITY_SCORER = new InstantiatorDefinitions.CompatibilityScorer() {
		@Override
		public int score(InstantiatorDefinition id) {
			Class<?> type = TypeHelper.toBoxedClass(id.getParameters()[0].getType());

			if (type == String.class || type == CharSequence.class) {
				return 10;
			}
			if (Number.class.isAssignableFrom(type)) {
				return 9;
			}
			if (Date.class.isAssignableFrom(type)) {
				return 8;
			}
			return 0;
		}
	};

	private final CellValueReaderFactory cellValueReaderFactory;
	private final MapperBuilderErrorHandler mapperBuilderErrorHandler;

	public CellSetterFactory(CellValueReaderFactory cellValueReaderFactory, MapperBuilderErrorHandler mapperBuilderErrorHandler) {
		this.cellValueReaderFactory = cellValueReaderFactory;
		this.mapperBuilderErrorHandler = mapperBuilderErrorHandler;
	}
	
	@SuppressWarnings("unchecked")
	public <T,P> CellSetter<T> getPrimitiveCellSetter(Class<?> clazz, CellValueReader<? extends P> reader, Setter<? super T, ? super P> setter) {
		if (boolean.class.equals(clazz)) {
			return new BooleanCellSetter<T>(ObjectSetterFactory.toBooleanSetter((Setter<? super T, ? super Boolean>) setter), booleanReader(reader));
		} else if (byte.class.equals(clazz)) {
			return new ByteCellSetter<T>(ObjectSetterFactory.toByteSetter((Setter<? super T, ? super Byte>) setter), byteReader(reader));
		} else if (char.class.equals(clazz)) {
			return new CharCellSetter<T>(ObjectSetterFactory.toCharacterSetter((Setter<? super T, ? super Character>) setter), charReader(reader));
		} else if (short.class.equals(clazz)) {
			return new ShortCellSetter<T>(ObjectSetterFactory.toShortSetter((Setter<? super T, ? super Short>) setter), shortReader(reader));
		} else if (int.class.equals(clazz)) {
			return new IntCellSetter<T>(ObjectSetterFactory.toIntSetter((Setter<? super T, ? super Integer>) setter), intReader(reader));
		} else if (long.class.equals(clazz)) {
			return new LongCellSetter<T>(ObjectSetterFactory.toLongSetter((Setter<? super T, ? super Long>) setter), longReader(reader));
		} else if (float.class.equals(clazz)) {
			return new FloatCellSetter<T>(ObjectSetterFactory.toFloatSetter((Setter<? super T, ? super Float>) setter), floatReader(reader));
		} else if (double.class.equals(clazz)) {
			return new DoubleCellSetter<T>(ObjectSetterFactory.toDoubleSetter((Setter<? super T, ? super Double>) setter), doubleReader(reader));
		} 
		throw new IllegalArgumentException("Invalid primitive type " + clazz);
	}

	@SuppressWarnings("unchecked")
	private DoubleCellValueReader doubleReader(CellValueReader<?> reader) {
		if (reader instanceof DoubleCellValueReader)  {
			return (DoubleCellValueReader) reader;
		} else {
			return new BoxedDoubleCellValueReader((CellValueReader<Double>) reader);
		}
	}

	@SuppressWarnings("unchecked")
	private FloatCellValueReader floatReader(CellValueReader<?> reader) {
		if (reader instanceof FloatCellValueReader)  {
			return (FloatCellValueReader) reader;
		} else {
			return new BoxedFloatCellValueReader((CellValueReader<Float>) reader);
		}
	}

	@SuppressWarnings("unchecked")
	private LongCellValueReader longReader(CellValueReader<?> reader) {
		if (reader instanceof LongCellValueReader)  {
			return (LongCellValueReader) reader;
		} else {
			return new BoxedLongCellValueReader((CellValueReader<Long>) reader);
		}
	}

	@SuppressWarnings("unchecked")
	private IntegerCellValueReader intReader(CellValueReader<?> reader) {
		if (reader instanceof IntegerCellValueReader)  {
			return (IntegerCellValueReader) reader;
		} else {
			return new BoxedIntegerCellValueReader((CellValueReader<Integer>) reader);
		}
	}

	@SuppressWarnings("unchecked")
	private ShortCellValueReader shortReader(CellValueReader<?> reader) {
		if (reader instanceof ShortCellValueReader)  {
			return (ShortCellValueReader) reader;
		} else {
			return new BoxedShortCellValueReader((CellValueReader<Short>) reader);
		}
	}

	@SuppressWarnings("unchecked")
	private CharCellValueReader charReader(CellValueReader<?> reader) {
		if (reader instanceof CharCellValueReader)  {
			return (CharCellValueReader) reader;
		} else {
			return new BoxedCharCellValueReader((CellValueReader<Character>) reader);
		}
	}

	@SuppressWarnings("unchecked")
	private ByteCellValueReader byteReader(CellValueReader<?> reader) {
		if (reader instanceof ByteCellValueReader)  {
			return (ByteCellValueReader) reader;
		} else {
			return new BoxedByteCellValueReader((CellValueReader<Byte>) reader);
		}
	}

	@SuppressWarnings("unchecked")
	private BooleanCellValueReader booleanReader(CellValueReader<?> reader) {
		if (reader instanceof BooleanCellValueReader)  {
			return (BooleanCellValueReader) reader;
		} else {
			return new BoxedBooleanCellValueReader((CellValueReader<Boolean>) reader);
		}
	}

	@SuppressWarnings("unchecked")
	private <T,P> DelayedCellSetterFactory<T, P> getPrimitiveDelayedCellSetter(Class<?> clazz, CellValueReader<? extends P> reader, Setter<? super T, ? super P> setter) {
		if (boolean.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new BooleanDelayedCellSetterFactory<T>(ObjectSetterFactory.toBooleanSetter((Setter<? super T, ? super Boolean>) setter), booleanReader(reader));
		} else if (byte.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new ByteDelayedCellSetterFactory<T>(ObjectSetterFactory.toByteSetter((Setter<? super T, ? super Byte>) setter), byteReader(reader));
		} else if (char.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new CharDelayedCellSetterFactory<T>(ObjectSetterFactory.toCharacterSetter((Setter<? super T, ? super Character>) setter), charReader(reader));
		} else if (short.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new ShortDelayedCellSetterFactory<T>(ObjectSetterFactory.toShortSetter((Setter<? super T, ? super Short>) setter), shortReader(reader));
		} else if (int.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new IntDelayedCellSetterFactory<T>(ObjectSetterFactory.toIntSetter((Setter<? super T, ? super Integer>) setter), intReader(reader));
		} else if (long.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new LongDelayedCellSetterFactory<T>(ObjectSetterFactory.toLongSetter((Setter<? super T, ? super Long>) setter), longReader(reader));
		} else if (float.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new FloatDelayedCellSetterFactory<T>(ObjectSetterFactory.toFloatSetter((Setter<? super T, ? super Float>) setter), floatReader(reader));
		} else if (double.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new DoubleDelayedCellSetterFactory<T>(ObjectSetterFactory.toDoubleSetter((Setter<? super T, ? super Double>) setter), doubleReader(reader));
		} 
		throw new IllegalArgumentException("Invalid primitive type " + clazz);
	}

	@SuppressWarnings("unchecked")
    public <T, P> Getter<CsvMapperCellHandler<T>, P> newDelayedGetter(CsvColumnKey key, Type type) {
		Class<?> clazz = TypeHelper.toClass(type);
		Getter getter;
		int columnIndex = key.getIndex();
		if (clazz.isPrimitive()) {
			if (boolean.class.equals(clazz)) {
				getter = new BooleanDelayedGetter<T>(columnIndex);
			} else if (byte.class.equals(clazz)) {
				getter = new ByteDelayedGetter<T>(columnIndex);
			} else if (char.class.equals(clazz)) {
				getter = new CharDelayedGetter<T>(columnIndex);
			} else if (short.class.equals(clazz)) {
				getter = new ShortDelayedGetter<T>(columnIndex);
			} else if (int.class.equals(clazz)) {
				getter = new IntDelayedGetter<T>(columnIndex);
			} else if (long.class.equals(clazz)) {
				getter = new LongDelayedGetter<T>(columnIndex);
			} else if (float.class.equals(clazz)) {
				getter = new FloatDelayedGetter<T>(columnIndex);
			} else if (double.class.equals(clazz)) {
				getter = new DoubleDelayedGetter<T>(columnIndex);
			} else {
				throw new IllegalArgumentException("Unexpected primitive " + clazz);
			}
		} else {
			getter = new DelayedGetter<T, P>(columnIndex);
		}
		return getter;
	}

	@SuppressWarnings({"unchecked" })
	private <P> CellValueReader<P> getReader(PropertyMeta<?, P> pm, int index, CsvColumnDefinition columnDefinition, ParsingContextFactoryBuilder parsingContextFactoryBuilder) {
		CellValueReader<P> reader = null;

		if (columnDefinition.hasCustomSourceFrom(pm.getOwnerType())) {
			reader = (CellValueReader<P>) columnDefinition.getCustomReader();
		}

        if (reader == null) {
			reader = cellValueReaderFromFactory(pm, index, columnDefinition, parsingContextFactoryBuilder);
        }

		if (reader == null) {
			mapperBuilderErrorHandler.accessorNotFound("Could not find reader for "
					+ pm.getPath() + " type " + pm.getPropertyType() 
					+ " See " + ErrorDoc.toUrl("CSFM_GETTER_NOT_FOUND"));
		}

		return reader;
	}

	private <P> CellValueReader<P> cellValueReaderFromFactory(PropertyMeta<?, ?> pm, int index, CsvColumnDefinition columnDefinition, ParsingContextFactoryBuilder parsingContextFactoryBuilder) {

		Type propertyType = pm.getPropertyType();

		CellValueReader<P> reader = null;
		if (columnDefinition.hasCustomReaderFactory()) {
            CellValueReaderFactory factory = columnDefinition.getCustomCellValueReaderFactory();
            reader = factory.getReader(propertyType, index, columnDefinition, parsingContextFactoryBuilder);
        }

		if (reader == null) {
            reader = cellValueReaderFactory.getReader(propertyType, index, columnDefinition, parsingContextFactoryBuilder);
        }

		if (reader == null) {
			if (!pm.isSelf()) {
				final ClassMeta<?> classMeta = pm.getPropertyClassMeta();
				InstantiatorDefinition id = InstantiatorDefinitions.lookForCompatibleOneArgument(classMeta.getInstantiatorDefinitions(), COMPATIBILITY_SCORER);

				if (id != null) {
					final Parameter parameter = id.getParameters()[0];
					// look for constructor property matching name
					final PropertyMeta<?, Object> property = classMeta.newPropertyFinder(new Predicate<PropertyMeta<?, ?>>() {
						@Override
						public boolean test(PropertyMeta<?, ?> propertyMeta) {
							return propertyMeta.isConstructorProperty()
									|| propertyMeta.isSubProperty() && ((SubPropertyMeta)propertyMeta).getOwnerProperty().isConstructorProperty();
						}
					}).findProperty(DefaultPropertyNameMatcher.exact(parameter.getName()), columnDefinition.properties());
					reader = cellValueReaderFromFactory(property, index, columnDefinition, parsingContextFactoryBuilder);
					if (reader != null) {
						Instantiator<P, P> instantiator =
								classMeta.getReflectionService().getInstantiatorFactory().getOneArgIdentityInstantiator(id, classMeta.getReflectionService().builderIgnoresNullValues());
						return
								new InstantiatorOnReader<P, P>(instantiator, reader);
					}
				}
			}
		}
		return reader;
	}

	@SuppressWarnings("unchecked")
	public <T, P> CellSetter<T> getCellSetter(PropertyMeta<T, P> prop, int index, CsvColumnDefinition columnDefinition, ParsingContextFactoryBuilder parsingContextFactoryBuilder) {
		Class<? extends P> propertyClass = (Class<? extends P>) TypeHelper.toClass(prop.getPropertyType());

		CellValueReader<? extends P> reader = getReader(prop, index, columnDefinition, parsingContextFactoryBuilder);

		if (propertyClass.isPrimitive()) {
			return getPrimitiveCellSetter(propertyClass, reader, getSetter(prop));
		} else {
			return new CellSetterImpl<T, P>(reader, getSetter(prop));
		}
	}

    public <T, P> DelayedCellSetterFactory<T, P> getDelayedCellSetter(PropertyMeta<T, P> prop, int index, CsvColumnDefinition columnDefinition, ParsingContextFactoryBuilder parsingContextFactoryBuilder) {
		Class<? extends P> propertyClass = TypeHelper.toClass(prop.getPropertyType());

		CellValueReader<? extends P> reader = getReader(prop, index, columnDefinition, parsingContextFactoryBuilder);

		DelayedCellSetterFactory<T, P> factory;

		final Setter<? super T, ? super P> setter = prop.isConstructorProperty() ? null  : getSetter(prop);

		if (propertyClass.isPrimitive()) {
			factory = getPrimitiveDelayedCellSetter(propertyClass, reader, setter);
		} else {
			factory = new DelayedCellSetterFactoryImpl<T, P>(reader, setter);
		}

		final DefaultValueProperty defaultValueProperty = columnDefinition.lookFor(DefaultValueProperty.class);

		if (defaultValueProperty != null) {
			factory = new DefaultValueDelayedCallSetterFactory<T, P>(factory, defaultValueProperty, setter);
		}

		return factory;


	}

	private <T, P> Setter<? super T, ? super P> getSetter(PropertyMeta<T, P> prop) {
		Setter<? super T, ? super P> setter = prop.getSetter();

		if (NullSetter.isNull(setter)) {
			return null;
		} else {
			return setter;
		}
	}
}
