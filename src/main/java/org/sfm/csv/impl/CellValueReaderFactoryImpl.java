package org.sfm.csv.impl;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.CellValueReaderFactory;
import org.sfm.csv.CsvColumnDefinition;
import org.sfm.csv.impl.cellreader.*;
import org.sfm.csv.impl.cellreader.joda.JodaTimeCellValueReaderHelper;
import org.sfm.csv.ParsingContextFactoryBuilder;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
//IFJAVA8_START
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.sfm.csv.impl.cellreader.time.JavaLocalDateCellValueReader;
import org.sfm.csv.impl.cellreader.time.JavaLocalDateTimeCellValueReader;
import org.sfm.csv.impl.cellreader.time.JavaLocalTimeCellValueReader;
//IFJAVA8_END

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
		READERS.put(Object.class,    new StringCellValueReader());
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <P> CellValueReader<P> getReader(Type propertyType, int index, CsvColumnDefinition columnDefinition, ParsingContextFactoryBuilder parsingContextFactoryBuilder) {
		Class<? extends P> propertyClass =  TypeHelper.toClass(propertyType);

		CellValueReader<P> reader;

		if (propertyClass.equals(Date.class)) {
			DateCellValueReader dateCellValueReader = new DateCellValueReader(index, columnDefinition.dateFormat(), columnDefinition.getTimeZone());
			reader = (CellValueReader<P>) dateCellValueReader;
			parsingContextFactoryBuilder.addParsingContextProvider(index, dateCellValueReader);
		//IFJAVA8_START
		} else if (propertyClass.equals(LocalDate.class)) {
			reader = (CellValueReader<P>) new JavaLocalDateCellValueReader(columnDefinition.dateFormat(), columnDefinition.getTimeZone());
		} else if (propertyClass.equals(LocalDateTime.class)) {
			reader = (CellValueReader<P>) new JavaLocalDateTimeCellValueReader(columnDefinition.dateFormat(), columnDefinition.getTimeZone());
		} else if (propertyClass.equals(LocalTime.class)) {
			reader = (CellValueReader<P>) new JavaLocalTimeCellValueReader(columnDefinition.dateFormat(), columnDefinition.getTimeZone());
		//IFJAVA8_END
		} else if (Calendar.class.equals(propertyClass)) {
            CalendarCellValueReader calendarCellValueReader = new CalendarCellValueReader(index, columnDefinition.dateFormat(), columnDefinition.getTimeZone());
            reader = (CellValueReader<P>) calendarCellValueReader;
            parsingContextFactoryBuilder.addParsingContextProvider(index, calendarCellValueReader);
		}  else if (Enum.class.isAssignableFrom(propertyClass)) {
			reader = new EnumCellValueReader(propertyClass);
		} else {
			reader = (CellValueReader<P>) JodaTimeCellValueReaderHelper.getReader(propertyClass, columnDefinition);
		}

		if (reader == null) {
			reader = getCellValueTransformer(propertyClass);
		}

		return reader;
	}

    @SuppressWarnings("unchecked")
	private <P> CellValueReader<P> getCellValueTransformer(Class<? extends P> propertyType) {
		return (CellValueReader<P>) READERS.get(propertyType);
	}


}
