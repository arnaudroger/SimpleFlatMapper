package org.sfm.csv.impl;


import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;
import org.sfm.csv.CellValueReader;
import org.sfm.csv.CsvColumnDefinition;
import org.sfm.csv.ParsingContextFactoryBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
//IFJAVA8_START
import java.time.format.DateTimeFormatter;
//IFJAVA8_END

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CellValueReaderFactoryImplTest {


    private final CellValueReaderFactoryImpl cellValueReaderFactory = new CellValueReaderFactoryImpl();

    @Test
    public void testDoesNotReaderAReaderForJavaSqlDate() {
        assertNull(cellValueReaderFactory.getReader(java.sql.Date.class, 1, null, null));
    }

    @Test
    public void testReturnStringForObject() {
        CellValueReader<?> reader = cellValueReaderFactory.getReader(Object.class, 1, null, null);
        String object = "string";
        assertEquals(object, reader.read(object.toCharArray(), 0, object.length(), null));
    }

    @Test
    public void testCalendar() throws ParseException {
        String date = "20150128";
        Date dd = new SimpleDateFormat("yyyyMMdd").parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dd);

        ParsingContextFactoryBuilder parsingContextFactoryBuilder = new ParsingContextFactoryBuilder(1);

        CellValueReader<?> reader = cellValueReaderFactory.getReader(Calendar.class, 0, CsvColumnDefinition.dateFormatDefinition("yyyyMMdd"), parsingContextFactoryBuilder);
        assertEquals(cal, reader.read(date.toCharArray(), 0, date.length(), parsingContextFactoryBuilder.newFactory().newContext()));
    }

    @Test
    public void testJodaDateTime() {
        String date = "20150128";
        DateTime dateTime = DateTimeFormat.forPattern("yyyyMMdd").parseDateTime(date);
        CellValueReader<?> reader = cellValueReaderFactory.getReader(DateTime.class, 0, CsvColumnDefinition.dateFormatDefinition("yyyyMMdd"), null);
        assertEquals(dateTime, reader.read(date.toCharArray(), 0, date.length(), null));
    }
    @Test
    public void testJodaLocalDate() {
        String date = "20150128";
        LocalDate localDate = DateTimeFormat.forPattern("yyyyMMdd").parseLocalDate(date);
        CellValueReader<?> reader = cellValueReaderFactory.getReader(LocalDate.class, 0, CsvColumnDefinition.dateFormatDefinition("yyyyMMdd"), null);
        assertEquals(localDate, reader.read(date.toCharArray(), 0, date.length(), null));
    }
    @Test
    public void testJodaLocalDateTime() {
        String date = "20150128";
        LocalDateTime localDateTime = DateTimeFormat.forPattern("yyyyMMdd").parseLocalDateTime(date);
        CellValueReader<?> reader = cellValueReaderFactory.getReader(LocalDateTime.class, 0, CsvColumnDefinition.dateFormatDefinition("yyyyMMdd"), null);
        assertEquals(localDateTime, reader.read(date.toCharArray(), 0, date.length(), null));
    }
    @Test
    public void testJodaLocalTime() {
        String date = "20150128";
        LocalTime localTime = DateTimeFormat.forPattern("yyyyMMdd").parseLocalTime(date);
        CellValueReader<?> reader = cellValueReaderFactory.getReader(LocalTime.class, 0, CsvColumnDefinition.dateFormatDefinition("yyyyMMdd"), null);
        assertEquals(localTime, reader.read(date.toCharArray(), 0, date.length(), null));
    }


    //IFJAVA8_START
    @Test
    public void testJavaLocalDate() throws Exception {
        String date = "20150128";
        final DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd");
        java.time.LocalDate localDate = java.time.LocalDate.parse(date, yyyyMMdd);
        CellValueReader<?> reader = cellValueReaderFactory.getReader(java.time.LocalDate.class, 0, CsvColumnDefinition.dateFormatDefinition("yyyyMMdd"), null);
        assertEquals(localDate, reader.read(date.toCharArray(), 0, date.length(), null));
    }

    //IFJAVA8_END

}
