package org.sfm.csv.impl;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.CsvColumnDefinition;
import org.sfm.csv.CsvColumnKey;
import org.sfm.csv.impl.cellreader.*;
import org.sfm.csv.impl.primitive.*;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class CellSetterFactory {

	private static final Map<Class<?>, CellValueReader<?>> READERS = new HashMap<Class<?>, CellValueReader<?>>();

	static {
		READERS.put(boolean.class, new BooleanCellValueReaderImpl());
		READERS.put(byte.class, new ByteCellValueReaderImpl());
		READERS.put(char.class, new CharCellValueReaderImpl());
		READERS.put(short.class, new ShortCellValueReaderImpl());
		READERS.put(int.class, new IntegerCellValueReaderImpl());
		READERS.put(long.class, new LongCellValueReaderImpl());
		READERS.put(float.class, new FloatCellValueReaderImpl());
		READERS.put(double.class, new DoubleCellValueReaderImpl());
		READERS.put(Boolean.class, new BooleanCellValueReaderImpl());
		READERS.put(Byte.class, new ByteCellValueReaderImpl());
		READERS.put(Character.class, new CharCellValueReaderImpl());
		READERS.put(Short.class, new ShortCellValueReaderImpl());
		READERS.put(Integer.class, new IntegerCellValueReaderImpl());
		READERS.put(Long.class, new LongCellValueReaderImpl());
		READERS.put(Float.class, new FloatCellValueReaderImpl());
		READERS.put(Double.class, new DoubleCellValueReaderImpl());
		READERS.put(String.class,    new StringCellValueReader());
	}
	

	
	public <T,P> CellSetter<T> getPrimitiveCellSetter(Class<?> clazz, Setter<T, P> setter, CsvColumnDefinition columnDefinition) {
		if (boolean.class.equals(clazz)) {
			BooleanCellValueReader reader = booleanReader(columnDefinition);
			return new BooleanCellSetter<T>(SetterFactory.toBooleanSetter(setter), reader);
		} else if (byte.class.equals(clazz)) {
			ByteCellValueReader reader = byteReader(columnDefinition);
			return new ByteCellSetter<T>(SetterFactory.toByteSetter(setter), reader);
		} else if (char.class.equals(clazz)) {
			CharCellValueReader reader = charReader(columnDefinition);
			return new CharCellSetter<T>(SetterFactory.toCharacterSetter(setter), reader);
		} else if (short.class.equals(clazz)) {
			ShortCellValueReader reader = shortReader(columnDefinition);
			return new ShortCellSetter<T>(SetterFactory.toShortSetter(setter), reader);
		} else if (int.class.equals(clazz)) {
			IntegerCellValueReader reader = intReader(columnDefinition);
			return new IntCellSetter<T>(SetterFactory.toIntSetter(setter), reader);
		} else if (long.class.equals(clazz)) {
			LongCellValueReader reader = longReader(columnDefinition);
			return new LongCellSetter<T>(SetterFactory.toLongSetter(setter), reader);
		} else if (float.class.equals(clazz)) {
			FloatCellValueReader reader = floatReader(columnDefinition);
			return new FloatCellSetter<T>(SetterFactory.toFloatSetter(setter), reader);
		} else if (double.class.equals(clazz)) {
			DoubleCellValueReader reader = doubleReader(columnDefinition);
			return new DoubleCellSetter<T>(SetterFactory.toDoubleSetter(setter), reader);
		} 
		throw new IllegalArgumentException("Invalid primitive type " + clazz);
	}

	private DoubleCellValueReader doubleReader(CsvColumnDefinition columnDefinition) {
		DoubleCellValueReader reader;
		if (columnDefinition.hasCustomReader()) {
            CellValueReader<?> customReader = columnDefinition.getCustomReader();
            if (customReader instanceof DoubleCellValueReader) {
                reader = (DoubleCellValueReader) customReader;
            } else {
                reader = new DoubleCellValueReaderUnbox((CellValueReader<Double>)customReader);
            }
        } else {
            reader = new DoubleCellValueReaderImpl();
        }
		return reader;
	}

	private FloatCellValueReader floatReader(CsvColumnDefinition columnDefinition) {
		FloatCellValueReader reader;
		if (columnDefinition.hasCustomReader()) {
            CellValueReader<?> customReader = columnDefinition.getCustomReader();
            if (customReader instanceof FloatCellValueReader) {
                reader = (FloatCellValueReader) customReader;
            } else {
                reader = new FloatCellValueReaderUnbox((CellValueReader<Float>)customReader);
            }
        } else {
            reader = new FloatCellValueReaderImpl();
        }
		return reader;
	}

	private LongCellValueReader longReader(CsvColumnDefinition columnDefinition) {
		LongCellValueReader reader;
		if (columnDefinition.hasCustomReader()) {
            CellValueReader<?> customReader = columnDefinition.getCustomReader();
            if (customReader instanceof LongCellValueReader) {
                reader = (LongCellValueReader) customReader;
            } else {
                reader = new LongCellValueReaderUnbox((CellValueReader<Long>)customReader);
            }
        } else {
            reader = new LongCellValueReaderImpl();
        }
		return reader;
	}

	private IntegerCellValueReader intReader(CsvColumnDefinition columnDefinition) {
		IntegerCellValueReader reader;
		if (columnDefinition.hasCustomReader()) {
            CellValueReader<?> customReader = columnDefinition.getCustomReader();
            if (customReader instanceof IntegerCellValueReader) {
                reader = (IntegerCellValueReader) customReader;
            } else {
                reader = new IntegerCellValueReaderUnbox((CellValueReader<Integer>)customReader);
            }
        } else {
            reader = new IntegerCellValueReaderImpl();
        }
		return reader;
	}

	private ShortCellValueReader shortReader(CsvColumnDefinition columnDefinition) {
		ShortCellValueReader reader;
		if (columnDefinition.hasCustomReader()) {
            CellValueReader<?> customReader = columnDefinition.getCustomReader();
            if (customReader instanceof ShortCellValueReader) {
                reader = (ShortCellValueReader) customReader;
            } else {
                reader = new ShortCellValueReaderUnbox((CellValueReader<Short>)customReader);
            }
        } else {
            reader = new ShortCellValueReaderImpl();
        }
		return reader;
	}

	private CharCellValueReader charReader(CsvColumnDefinition columnDefinition) {
		CharCellValueReader reader;
		if (columnDefinition.hasCustomReader()) {
            CellValueReader<?> customReader = columnDefinition.getCustomReader();
            if (customReader instanceof CharCellValueReader) {
                reader = (CharCellValueReader) customReader;
            } else {
                reader = new CharCellValueReaderUnbox((CellValueReader<Character>)customReader);
            }
        } else {
            reader = new CharCellValueReaderImpl();
        }
		return reader;
	}

	private ByteCellValueReader byteReader(CsvColumnDefinition columnDefinition) {
		ByteCellValueReader reader;
		if (columnDefinition.hasCustomReader()) {
            CellValueReader<?> customReader = columnDefinition.getCustomReader();
            if (customReader instanceof BooleanCellValueReader) {
                reader = (ByteCellValueReader) customReader;
            } else {
                reader = new ByteCellValueReaderUnbox((CellValueReader<Byte>)customReader);
            }
        } else {
            reader = new ByteCellValueReaderImpl();
        }
		return reader;
	}

	private BooleanCellValueReader booleanReader(CsvColumnDefinition columnDefinition) {
		BooleanCellValueReader reader;
		if (columnDefinition.hasCustomReader()) {
            CellValueReader<?> customReader = columnDefinition.getCustomReader();
            if (customReader instanceof BooleanCellValueReader) {
                reader = (BooleanCellValueReader) customReader;
            } else {
                reader = new BooleanCellValueReaderUnbox((CellValueReader<Boolean>)customReader);
            }
        } else {
            reader = new BooleanCellValueReaderImpl();
        }
		return reader;
	}

	@SuppressWarnings("unchecked")
	public <T,P> DelayedCellSetterFactory<T, P> getPrimitiveDelayedCellSetter(Class<?> clazz, Setter<T, P> setter, CsvColumnDefinition columnDefinition) {
		if (boolean.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new BooleanDelayedCellSetterFactory<T>(SetterFactory.toBooleanSetter(setter), booleanReader(columnDefinition));
		} else if (byte.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new ByteDelayedCellSetterFactory<T>(SetterFactory.toByteSetter(setter), byteReader(columnDefinition));
		} else if (char.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new CharDelayedCellSetterFactory<T>(SetterFactory.toCharacterSetter(setter), charReader(columnDefinition));
		} else if (short.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new ShortDelayedCellSetterFactory<T>(SetterFactory.toShortSetter(setter), shortReader(columnDefinition));
		} else if (int.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new IntDelayedCellSetterFactory<T>(SetterFactory.toIntSetter(setter), intReader(columnDefinition));
		} else if (long.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new LongDelayedCellSetterFactory<T>(SetterFactory.toLongSetter(setter), longReader(columnDefinition));
		} else if (float.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new FloatDelayedCellSetterFactory<T>(SetterFactory.toFloatSetter(setter), floatReader(columnDefinition));
		} else if (double.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new DoubleDelayedCellSetterFactory<T>(SetterFactory.toDoubleSetter(setter), doubleReader(columnDefinition));
		} 
		throw new IllegalArgumentException("Invalid primitive type " + clazz);
	}

	public <T> Getter<DelayedCellSetter<T, ?>[], ?> newDelayedGetter(CsvColumnKey key, Type type) {
		Class<?> clazz = TypeHelper.toClass(type);
		Getter<DelayedCellSetter<T, ?>[], ?> getter;
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

	private <T, P> CellValueReader<P> getReaderForSetter(Type type, Setter<T, P> setter, int index, CsvColumnDefinition columnDefinition) {
		Class<P> propertyType = TypeHelper.toClass(type);
		CellValueReader<P> reader = getReader(propertyType, index, columnDefinition);
		return reader;
	}

	@SuppressWarnings({"unchecked" })
	private <P> CellValueReader<P> getReader(Class<P> propertyType, int index, CsvColumnDefinition columnDefinition) {
		CellValueReader<P> reader = null;

		if (columnDefinition.hasCustomReader()) {
			reader = (CellValueReader<P>) columnDefinition.getCustomReader();
		}

		if (reader == null) {
			reader = getKnownReader(propertyType, index);
		}

		if (reader == null) {
			// check if has a one arg construct
			final Constructor<?>[] constructors = propertyType.getConstructors();
			if (constructors != null && constructors.length == 1 && constructors[0].getParameterTypes().length == 1) {
				final Constructor<P> constructor = (Constructor<P>) constructors[0];
				CellValueReader<?> innerReader =   getKnownReader(constructor.getParameterTypes()[0], index);
				
				if (innerReader != null) {
					reader = new ConstructorOnReader<P>(constructor, innerReader);
				}
			}
		}
		
		if (reader == null) {
			throw new ParsingException("No cell reader for " + propertyType);
		}
		return reader;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <P> CellValueReader<P> getKnownReader(Class<P> propertyType,
			int index) {
		CellValueReader<P> reader;
		if (Date.class.isAssignableFrom(propertyType)) {
			reader = (CellValueReader<P>) new DateCellValueReader(index);
		} else if (Enum.class.isAssignableFrom(propertyType)) {
			reader = new EnumCellValueReader(propertyType);
		} else {
			reader = getCellValueTransformer(propertyType);
		}
		return reader;
	}

	@SuppressWarnings("unchecked")
	private <P> CellValueReader<P> getCellValueTransformer(Class<P> propertyType) {
		return (CellValueReader<P>) READERS.get(propertyType);
	}


	public <T,P> CellSetter<T> getCellSetter(Type propertyType, Setter<T, P> setter, int index, CsvColumnDefinition columnDefinition) {
		Class<?> propertyClass = TypeHelper.toClass(propertyType);

		if (propertyClass.isPrimitive()) {
			return getPrimitiveCellSetter(propertyClass, setter, columnDefinition);
		}

		CellValueReader<P> readerForSetter = getReaderForSetter(propertyType, setter, index, columnDefinition);

		return new CellSetterImpl<T, P>(readerForSetter, setter) ;
	}

	public <T, P> DelayedCellSetterFactory<T, P> getDelayedCellSetter(Type propertyType, Setter<T, P> setter, int index, CsvColumnDefinition columnDefinition) {
		Class<?> propertyClass = TypeHelper.toClass(propertyType);
		
		if (propertyClass.isPrimitive()) {
			return getPrimitiveDelayedCellSetter(propertyClass, setter, columnDefinition);
		}

		CellValueReader<P> reader = getReaderForSetter(propertyType, setter, index, columnDefinition);

		return new DelayedCellSetterFactoryImpl<T, P>(reader, setter);
	}
	@SuppressWarnings("unchecked")
	public <T, P> DelayedCellSetterFactory<T, P> getDelayedCellSetter(Type type, int index, CsvColumnDefinition columnDefinition) {
		Class<?> propertyClass = TypeHelper.toClass(type);
		
		if (propertyClass.isPrimitive()) {
			return getPrimitiveDelayedCellSetter(propertyClass, null, columnDefinition);
		}

		CellValueReader<P> reader = getReader((Class<P>) TypeHelper.toClass(type), index, columnDefinition);

		return new DelayedCellSetterFactoryImpl<T, P>(reader, null);
	}
}
