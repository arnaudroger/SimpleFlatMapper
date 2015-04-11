package org.sfm.csv.impl;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.CellValueReaderFactory;
import org.sfm.csv.CsvColumnDefinition;
import org.sfm.csv.CsvColumnKey;
import org.sfm.csv.impl.cellreader.*;
import org.sfm.csv.impl.primitive.*;
import org.sfm.csv.ParsingContextFactoryBuilder;
import org.sfm.reflect.*;
import org.sfm.reflect.impl.NullSetter;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.PropertyMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

public final class CellSetterFactory {

	private final CellValueReaderFactory cellValueReaderFactory;

	public CellSetterFactory(CellValueReaderFactory cellValueReaderFactory) {
		this.cellValueReaderFactory = cellValueReaderFactory;
	}
	
	public <T,P> CellSetter<T> getPrimitiveCellSetter(Class<?> clazz, CellValueReader<? extends P> reader,  Setter<T, ? super P> setter) {
		if (boolean.class.equals(clazz)) {
			return new BooleanCellSetter<T>(ObjectSetterFactory.toBooleanSetter(setter), booleanReader(reader));
		} else if (byte.class.equals(clazz)) {
			return new ByteCellSetter<T>(ObjectSetterFactory.toByteSetter(setter), byteReader(reader));
		} else if (char.class.equals(clazz)) {
			return new CharCellSetter<T>(ObjectSetterFactory.toCharacterSetter(setter), charReader(reader));
		} else if (short.class.equals(clazz)) {
			return new ShortCellSetter<T>(ObjectSetterFactory.toShortSetter(setter), shortReader(reader));
		} else if (int.class.equals(clazz)) {
			return new IntCellSetter<T>(ObjectSetterFactory.toIntSetter(setter), intReader(reader));
		} else if (long.class.equals(clazz)) {
			return new LongCellSetter<T>(ObjectSetterFactory.toLongSetter(setter), longReader(reader));
		} else if (float.class.equals(clazz)) {
			return new FloatCellSetter<T>(ObjectSetterFactory.toFloatSetter(setter), floatReader(reader));
		} else if (double.class.equals(clazz)) {
			return new DoubleCellSetter<T>(ObjectSetterFactory.toDoubleSetter(setter), doubleReader(reader));
		} 
		throw new IllegalArgumentException("Invalid primitive type " + clazz);
	}

	@SuppressWarnings("unchecked")
	private DoubleCellValueReader doubleReader(CellValueReader<?> reader) {
		if (reader instanceof DoubleCellValueReader)  {
			return (DoubleCellValueReader) reader;
		} else {
			return new DoubleCellValueReaderUnbox((CellValueReader<Double>) reader);
		}
	}

	@SuppressWarnings("unchecked")
	private FloatCellValueReader floatReader(CellValueReader<?> reader) {
		if (reader instanceof FloatCellValueReader)  {
			return (FloatCellValueReader) reader;
		} else {
			return new FloatCellValueReaderUnbox((CellValueReader<Float>) reader);
		}
	}

	@SuppressWarnings("unchecked")
	private LongCellValueReader longReader(CellValueReader<?> reader) {
		if (reader instanceof LongCellValueReader)  {
			return (LongCellValueReader) reader;
		} else {
			return new LongCellValueReaderUnbox((CellValueReader<Long>) reader);
		}
	}

	@SuppressWarnings("unchecked")
	private IntegerCellValueReader intReader(CellValueReader<?> reader) {
		if (reader instanceof IntegerCellValueReader)  {
			return (IntegerCellValueReader) reader;
		} else {
			return new IntegerCellValueReaderUnbox((CellValueReader<Integer>) reader);
		}
	}

	@SuppressWarnings("unchecked")
	private ShortCellValueReader shortReader(CellValueReader<?> reader) {
		if (reader instanceof ShortCellValueReader)  {
			return (ShortCellValueReader) reader;
		} else {
			return new ShortCellValueReaderUnbox((CellValueReader<Short>) reader);
		}
	}

	@SuppressWarnings("unchecked")
	private CharCellValueReader charReader(CellValueReader<?> reader) {
		if (reader instanceof CharCellValueReader)  {
			return (CharCellValueReader) reader;
		} else {
			return new CharCellValueReaderUnbox((CellValueReader<Character>) reader);
		}
	}

	@SuppressWarnings("unchecked")
	private ByteCellValueReader byteReader(CellValueReader<?> reader) {
		if (reader instanceof ByteCellValueReader)  {
			return (ByteCellValueReader) reader;
		} else {
			return new ByteCellValueReaderUnbox((CellValueReader<Byte>) reader);
		}
	}

	@SuppressWarnings("unchecked")
	private BooleanCellValueReader booleanReader(CellValueReader<?> reader) {
		if (reader instanceof BooleanCellValueReader)  {
			return (BooleanCellValueReader) reader;
		} else {
			return new BooleanCellValueReaderUnbox((CellValueReader<Boolean>) reader);
		}
	}

	@SuppressWarnings("unchecked")
	private <T,P> DelayedCellSetterFactory<T, P> getPrimitiveDelayedCellSetter(Class<?> clazz, CellValueReader<? extends P> reader, Setter<T, ? super P> setter) {
		if (boolean.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new BooleanDelayedCellSetterFactory<T>(ObjectSetterFactory.toBooleanSetter(setter), booleanReader(reader));
		} else if (byte.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new ByteDelayedCellSetterFactory<T>(ObjectSetterFactory.toByteSetter(setter), byteReader(reader));
		} else if (char.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new CharDelayedCellSetterFactory<T>(ObjectSetterFactory.toCharacterSetter(setter), charReader(reader));
		} else if (short.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new ShortDelayedCellSetterFactory<T>(ObjectSetterFactory.toShortSetter(setter), shortReader(reader));
		} else if (int.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new IntDelayedCellSetterFactory<T>(ObjectSetterFactory.toIntSetter(setter), intReader(reader));
		} else if (long.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new LongDelayedCellSetterFactory<T>(ObjectSetterFactory.toLongSetter(setter), longReader(reader));
		} else if (float.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new FloatDelayedCellSetterFactory<T>(ObjectSetterFactory.toFloatSetter(setter), floatReader(reader));
		} else if (double.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new DoubleDelayedCellSetterFactory<T>(ObjectSetterFactory.toDoubleSetter(setter), doubleReader(reader));
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
			getter = new DelayedGetter<T>(columnIndex);
		}
		return getter;
	}

	@SuppressWarnings({"unchecked" })
	private <P> CellValueReader<P> getReader(ClassMeta<P> propertyType, int index, CsvColumnDefinition columnDefinition, ParsingContextFactoryBuilder parsingContextFactoryBuilder) {
		CellValueReader<P> reader = null;

		if (columnDefinition.hasCustomSource()) {
			reader = (CellValueReader<P>) columnDefinition.getCustomReader();
		}

        CellValueReaderFactory factory = cellValueReaderFactory;
        if (columnDefinition.hasCustomReaderFactory()) {
            factory = columnDefinition.getCustomCellValueReaderFactory();
        }

        if (reader == null) {
            reader = factory.getReader(propertyType.getType(), index, columnDefinition, parsingContextFactoryBuilder);
        }

		if (reader == null) {
			for(InstantiatorDefinition id : propertyType.getInstantiatorDefinitions()) {
				if (id.getParameters().length == 1) {
					final Type sourceType = id.getParameters()[0].getGenericType();
					reader = factory.getReader(sourceType, index, columnDefinition, parsingContextFactoryBuilder);
					if (reader != null) {
						Instantiator instantiator =
								propertyType.getReflectionService().getInstantiatorFactory().getOneArgIdentityInstantiator(id);
						return
								new InstantiatorOnReader(instantiator, reader);
					}
				}
			}
		}

//		if (reader == null && false) {
//			// check if has a one arg construct
//			final Constructor<?>[] constructors = propertyType.getConstructors();
//			if (constructors != null && constructors.length == 1 && constructors[0].getParameterTypes().length == 1) {
//				final Constructor<P> constructor = (Constructor<P>) constructors[0];
//				CellValueReader<?> innerReader = factory.<P>getReader(constructor.getParameterTypes()[0], index, columnDefinition, parsingContextFactoryBuilder);
//
//				if (innerReader != null) {
//					reader = new ConstructorOnReader<P>(constructor, innerReader);
//				}
//			}
//		}

		if (reader == null) {
			throw new ParsingException("No cell reader for " + propertyType);
		}
		return reader;
	}


    public <T, P> CellSetter<T> getCellSetter(PropertyMeta<T, P> prop, int index, CsvColumnDefinition columnDefinition, ParsingContextFactoryBuilder parsingContextFactoryBuilder) {
		Class<? extends P> propertyClass = (Class<? extends P>) TypeHelper.toClass(prop.getType());

		CellValueReader<? extends P> reader = getReader(prop.getClassMeta(), index, columnDefinition, parsingContextFactoryBuilder);

		if (propertyClass.isPrimitive()) {
			return getPrimitiveCellSetter(propertyClass, reader, getSetter(prop));
		} else {
			return new CellSetterImpl<T, P>(reader, getSetter(prop));
		}
	}

    public <T, P> DelayedCellSetterFactory<T, P> getDelayedCellSetter(PropertyMeta<T, P> prop, int index, CsvColumnDefinition columnDefinition, ParsingContextFactoryBuilder parsingContextFactoryBuilder) {
		Class<? extends P> propertyClass = TypeHelper.toClass(prop.getType());

		CellValueReader<? extends P> reader = getReader(prop.getClassMeta(), index, columnDefinition, parsingContextFactoryBuilder);

		if (propertyClass.isPrimitive()) {
			return getPrimitiveDelayedCellSetter(propertyClass, reader, getSetter(prop));
		} else {
			return new DelayedCellSetterFactoryImpl<T, P>(reader, getSetter(prop));
		}


	}

	private <T, P> Setter<T, P> getSetter(PropertyMeta<T, P> prop) {
		Setter<T, P> setter = prop.getSetter();

		if (NullSetter.isNull(setter)) {
			return null;
		} else {
			return setter;
		}
	}
}
