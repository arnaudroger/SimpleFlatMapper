package org.simpleflatmapper.csv.impl;

import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.converter.ConverterService;
import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.CellValueReaderFactory;
import org.simpleflatmapper.csv.CsvColumnDefinition;
import org.simpleflatmapper.csv.impl.cellreader.*;
import org.simpleflatmapper.csv.ParsingContextFactoryBuilder;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.simpleflatmapper.util.TypeHelper;

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
		READERS.put(CharSequence.class,    new StringCellValueReader());
		READERS.put(BigInteger.class,    new BigIntegerCellValueReader());
		READERS.put(BigDecimal.class,    new BigDecimalCellValueReader());
		READERS.put(Object.class,    new StringCellValueReader());
	}

	private ConverterService converterService = ConverterService.getInstance();

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <P> CellValueReader<P> getReader(Type propertyType, int index, CsvColumnDefinition columnDefinition, ParsingContextFactoryBuilder parsingContextFactoryBuilder) {
		Class<? extends P> propertyClass =  TypeHelper.toClass(propertyType);

		CellValueReader<P> reader = null;

		if (propertyClass.equals(Date.class)) {
			String[] patterns = columnDefinition.dateFormats();
			if (patterns.length == 1) {
				DateCellValueReader dateCellValueReader = new DateCellValueReader(index, patterns[0], columnDefinition.getTimeZone());
				reader = (CellValueReader<P>) dateCellValueReader;
				parsingContextFactoryBuilder.addParsingContextProvider(index, dateCellValueReader);
			} else {
				DateMultiFormatCellValueReader dateCellValueReader = new DateMultiFormatCellValueReader(index, patterns, columnDefinition.getTimeZone());
				reader = (CellValueReader<P>) dateCellValueReader;
				parsingContextFactoryBuilder.addParsingContextProvider(index, dateCellValueReader);
			}
		} else if (Calendar.class.equals(propertyClass)) {
			reader = (CellValueReader<P>) new CalendarCellValueReader(this.<Date>getReader(Date.class, index, columnDefinition, parsingContextFactoryBuilder));
		}  else if (Enum.class.isAssignableFrom(propertyClass)) {
			reader = new EnumCellValueReader(propertyClass);
		} else if (UUID.class.equals(propertyClass)) {
			reader = (CellValueReader<P>) new UUIDCellValueReader();
		}

		if (reader == null) {
			reader = getCellValueTransformer(propertyClass);
		}


		if (reader == null) {
			Converter<? super Object, ? extends P> converter = converterService.findConverter(CharSequence.class, propertyType, columnDefinition != null ? columnDefinition.properties() : new Object[0]);
			if (converter != null) {
				return new CharSequenceConverterCellValueReader<P>(converter);
			}
		}

		return reader;
	}

	@SuppressWarnings("unchecked")
	private <P> CellValueReader<P> getCellValueTransformer(Class<? extends P> propertyType) {
		return (CellValueReader<P>) READERS.get(propertyType);
	}


}
