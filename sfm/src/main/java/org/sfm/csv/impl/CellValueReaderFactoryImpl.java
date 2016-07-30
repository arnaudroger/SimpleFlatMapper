package org.sfm.csv.impl;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.CellValueReaderFactory;
import org.sfm.csv.CsvColumnDefinition;
import org.sfm.csv.impl.cellreader.*;
import org.sfm.csv.impl.cellreader.joda.JodaTimeCellValueReaderHelper;
import org.sfm.csv.ParsingContextFactoryBuilder;
import org.sfm.map.impl.JodaTimeClasses;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
//IFJAVA8_START
import java.time.*;

import org.sfm.map.column.time.*;
import org.sfm.csv.impl.cellreader.time.*;
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
		READERS.put(CharSequence.class,    new StringCellValueReader());
		READERS.put(Object.class,    new StringCellValueReader());
	}

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
		//IFJAVA8_START
		} else if (propertyClass.equals(LocalDate.class)) {
			reader = (CellValueReader<P>) new JavaLocalDateCellValueReader(JavaTimeHelper.getDateTimeFormatter(columnDefinition));
		} else if (propertyClass.equals(LocalDateTime.class)) {
			reader = (CellValueReader<P>) new JavaLocalDateTimeCellValueReader(JavaTimeHelper.getDateTimeFormatter(columnDefinition));
		} else if (propertyClass.equals(LocalTime.class)) {
			reader = (CellValueReader<P>) new JavaLocalTimeCellValueReader(JavaTimeHelper.getDateTimeFormatter(columnDefinition));
		} else if (propertyClass.equals(ZonedDateTime.class)) {
			reader = (CellValueReader<P>) new JavaZonedDateTimeCellValueReader(JavaTimeHelper.getDateTimeFormatter(columnDefinition));
		} else if (propertyClass.equals(OffsetTime.class)) {
			reader = (CellValueReader<P>) new JavaOffsetTimeCellValueReader(JavaTimeHelper.getDateTimeFormatter(columnDefinition));
		} else if (propertyClass.equals(OffsetDateTime.class)) {
			reader = (CellValueReader<P>) new JavaOffsetDateTimeCellValueReader(JavaTimeHelper.getDateTimeFormatter(columnDefinition));
		} else if (propertyClass.equals(Instant.class)) {
			reader = (CellValueReader<P>) new JavaInstantCellValueReader(JavaTimeHelper.getDateTimeFormatter(columnDefinition));
		} else if (propertyClass.equals(Year.class)) {
			reader = (CellValueReader<P>) new JavaYearCellValueReader(JavaTimeHelper.getDateTimeFormatter(columnDefinition));
		} else if (propertyClass.equals(YearMonth.class)) {
			reader = (CellValueReader<P>) new JavaYearMonthCellValueReader(JavaTimeHelper.getDateTimeFormatter(columnDefinition));
		//IFJAVA8_END
		}  else if (Enum.class.isAssignableFrom(propertyClass)) {
			reader = new EnumCellValueReader(propertyClass);
		} else if (JodaTimeClasses.isJoda(propertyClass)){
			reader = (CellValueReader<P>) JodaTimeCellValueReaderHelper.getReader(propertyClass, columnDefinition);
		} else if (UUID.class.equals(propertyClass)) {
			reader = (CellValueReader<P>) new UUIDCellValueReader();
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
