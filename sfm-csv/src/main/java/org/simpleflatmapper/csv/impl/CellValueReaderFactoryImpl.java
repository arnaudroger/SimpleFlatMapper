package org.simpleflatmapper.csv.impl;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.CellValueReaderFactory;
import org.simpleflatmapper.csv.CsvColumnDefinition;
import org.simpleflatmapper.csv.impl.cellreader.*;
import org.simpleflatmapper.csv.ParsingContextFactoryBuilder;
import org.simpleflatmapper.core.map.column.JodaTimeClasses;
import org.simpleflatmapper.csv.impl.cellreader.joda.JodaTimeCellValueReaderHelper;
import org.simpleflatmapper.core.reflect.TypeHelper;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
//IFJAVA8_START
import java.time.format.DateTimeFormatter;
import java.time.*;

import org.simpleflatmapper.core.map.column.time.*;
import org.simpleflatmapper.csv.impl.cellreader.time.*;
//IFJAVA8_END
import org.simpleflatmapper.core.utils.UnaryFactory;

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
			reader = (CellValueReader<P>)
					newJavaTime(columnDefinition,
							new UnaryFactory<DateTimeFormatter, CellValueReader<LocalDate>>() {
								@Override
								public CellValueReader<LocalDate> newInstance(DateTimeFormatter dateTimeFormatter) {
									return new JavaLocalDateCellValueReader(dateTimeFormatter);
								}
							});
		} else if (propertyClass.equals(LocalDateTime.class)) {
			reader = (CellValueReader<P>)
					newJavaTime(columnDefinition,
							new UnaryFactory<DateTimeFormatter, CellValueReader<LocalDateTime>>() {
								@Override
								public CellValueReader<LocalDateTime> newInstance(DateTimeFormatter dateTimeFormatter) {
									return new JavaLocalDateTimeCellValueReader(dateTimeFormatter);
								}
							});
		} else if (propertyClass.equals(LocalTime.class)) {
			reader = (CellValueReader<P>)
					newJavaTime(columnDefinition,
							new UnaryFactory<DateTimeFormatter, CellValueReader<LocalTime>>() {
								@Override
								public CellValueReader<LocalTime> newInstance(DateTimeFormatter dateTimeFormatter) {
									return new JavaLocalTimeCellValueReader(dateTimeFormatter);
								}
							});
		} else if (propertyClass.equals(ZonedDateTime.class)) {
			reader = (CellValueReader<P>)
					newJavaTime(columnDefinition,
							new UnaryFactory<DateTimeFormatter, CellValueReader<ZonedDateTime>>() {
								@Override
								public CellValueReader<ZonedDateTime> newInstance(DateTimeFormatter dateTimeFormatter) {
									return new JavaZonedDateTimeCellValueReader(dateTimeFormatter);
								}
							});
		} else if (propertyClass.equals(OffsetTime.class)) {
			reader = (CellValueReader<P>)
					newJavaTime(columnDefinition,
							new UnaryFactory<DateTimeFormatter, CellValueReader<OffsetTime>>() {
								@Override
								public CellValueReader<OffsetTime> newInstance(DateTimeFormatter dateTimeFormatter) {
									return new JavaOffsetTimeCellValueReader(dateTimeFormatter);
								}
							});
		} else if (propertyClass.equals(OffsetDateTime.class)) {
			reader = (CellValueReader<P>)
					newJavaTime(columnDefinition,
							new UnaryFactory<DateTimeFormatter, CellValueReader<OffsetDateTime>>() {
								@Override
								public CellValueReader<OffsetDateTime> newInstance(DateTimeFormatter dateTimeFormatter) {
									return new JavaOffsetDateTimeCellValueReader(dateTimeFormatter);
								}
							});
		} else if (propertyClass.equals(Instant.class)) {
			reader = (CellValueReader<P>)
					newJavaTime(columnDefinition,
							new UnaryFactory<DateTimeFormatter, CellValueReader<Instant>>() {
								@Override
								public CellValueReader<Instant> newInstance(DateTimeFormatter dateTimeFormatter) {
									return new JavaInstantCellValueReader(dateTimeFormatter);
								}
							});
		} else if (propertyClass.equals(Year.class)) {
			reader = (CellValueReader<P>)
					newJavaTime(columnDefinition,
							new UnaryFactory<DateTimeFormatter, CellValueReader<Year>>() {
								@Override
								public CellValueReader<Year> newInstance(DateTimeFormatter dateTimeFormatter) {
									return new JavaYearCellValueReader(dateTimeFormatter);
								}
							});
		} else if (propertyClass.equals(YearMonth.class)) {
			reader = (CellValueReader<P>)
					newJavaTime(columnDefinition,
							new UnaryFactory<DateTimeFormatter, CellValueReader<YearMonth>>() {
								@Override
								public CellValueReader<YearMonth> newInstance(DateTimeFormatter dateTimeFormatter) {
									return new JavaYearMonthCellValueReader(dateTimeFormatter);
								}
							});
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

	//IFJAVA8_START
	private <T>  CellValueReader<T> newJavaTime(CsvColumnDefinition csvColumnDefinition, UnaryFactory<DateTimeFormatter, CellValueReader<T>> unaryFactory) {
		DateTimeFormatter[] dateTimeFormatters = JavaTimeHelper.getDateTimeFormatters(csvColumnDefinition);
		if (dateTimeFormatters.length == 1) {
			return unaryFactory.newInstance(dateTimeFormatters[0]);
		} else {
			CellValueReader<T>[] readers = new CellValueReader[dateTimeFormatters.length];
			for(int i = 0; i < readers.length; i++) {
				readers[i] = unaryFactory.newInstance(dateTimeFormatters[i]);
			}
			return new MultiFormaterCellValueReader<T>(readers);
		}
	}
	//IFJAVA8_END

	@SuppressWarnings("unchecked")
	private <P> CellValueReader<P> getCellValueTransformer(Class<? extends P> propertyType) {
		return (CellValueReader<P>) READERS.get(propertyType);
	}


}
