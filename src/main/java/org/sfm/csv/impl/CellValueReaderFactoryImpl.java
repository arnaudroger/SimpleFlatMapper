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

public final class CellValueReaderFactoryImpl implements CellValueReaderFactory {

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
	

	

	private DoubleCellValueReader doubleReader(CsvColumnDefinition columnDefinition) {
		DoubleCellValueReader reader;
		if (columnDefinition.hasCustomSource()) {
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
		if (columnDefinition.hasCustomSource()) {
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
		if (columnDefinition.hasCustomSource()) {
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
		if (columnDefinition.hasCustomSource()) {
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
		if (columnDefinition.hasCustomSource()) {
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
		if (columnDefinition.hasCustomSource()) {
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
		if (columnDefinition.hasCustomSource()) {
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
		if (columnDefinition.hasCustomSource()) {
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


	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <P> CellValueReader<P> getReader(Type propertyType, int index) {
		Class<? extends P> propertyClass =  TypeHelper.toClass(propertyType);

		CellValueReader<P> reader;

		if (Date.class.isAssignableFrom(propertyClass)) {
			reader = (CellValueReader<P>) new DateCellValueReader(index);
		} else if (Enum.class.isAssignableFrom(propertyClass)) {
			reader = new EnumCellValueReader(propertyClass);
		} else {
			reader = getCellValueTransformer(propertyClass);
		}

		return reader;
	}

	@SuppressWarnings("unchecked")
	private <P> CellValueReader<P> getCellValueTransformer(Class<? extends P> propertyType) {
		return (CellValueReader<P>) READERS.get(propertyType);
	}


}
