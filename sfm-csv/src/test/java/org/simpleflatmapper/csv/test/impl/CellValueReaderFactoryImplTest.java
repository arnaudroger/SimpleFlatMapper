package org.simpleflatmapper.csv.test.impl;


import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;
import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.CsvColumnDefinition;
import org.simpleflatmapper.csv.ParsingContextFactoryBuilder;
import org.simpleflatmapper.csv.impl.CellValueReaderFactoryImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
//IFJAVA8_START
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.simpleflatmapper.map.property.time.JavaDateTimeFormatterProperty;
//IFJAVA8_END

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CellValueReaderFactoryImplTest {


    private final CellValueReaderFactoryImpl cellValueReaderFactory = new CellValueReaderFactoryImpl();

    @Test
    public void testDoesNotReaderAReaderForJavaSqlDate() throws ClassNotFoundException {
        assertNull(cellValueReaderFactory.getReader(Class.forName("java.sql.Date"), 1, null, null));
    }

    @Test
    public void testReturnStringForObject() {
        CellValueReader<?> reader = cellValueReaderFactory.getReader(Object.class, 1, null, null);
        String object = "string";
        assertEquals(object, reader.read(object.toCharArray(), 0, object.length(), null));
    }

    @Test
    public void testUUID() {
        UUID uuid = UUID.randomUUID();

        CellValueReader<?> reader = cellValueReaderFactory.getReader(UUID.class, 0, CsvColumnDefinition.identity(), null);
        final char[] chars = uuid.toString().toCharArray();
        assertEquals(uuid, reader.read(chars, 0, chars.length, null));
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
    public void testJodaLocalDateTimeWithDateTimeFormatter() {
        String date = "20150128";
        final org.joda.time.format.DateTimeFormatter yyyyMMdd = DateTimeFormat.forPattern("yyyyMMdd");
        LocalDateTime localDateTime = yyyyMMdd.parseLocalDateTime(date);
        CellValueReader<?> reader = cellValueReaderFactory.getReader(LocalDateTime.class, 0, CsvColumnDefinition.IDENTITY.add(yyyyMMdd), null);
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

    @Test
    public void testJavaLocalTime() throws Exception {
        String date = "12:03:56";
        final DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("HH:mm:ss");
        java.time.LocalTime localTime = java.time.LocalTime.parse(date, yyyyMMdd);
        CellValueReader<?> reader = cellValueReaderFactory.getReader(java.time.LocalTime.class, 0, CsvColumnDefinition.dateFormatDefinition("HH:mm:ss"), null);
        assertEquals(localTime, reader.read(date.toCharArray(), 0, date.length(), null));
    }


    @Test
    public void testJavaLocalDateTime() throws Exception {
        String date = "20150128 12:03:56";
        final DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");
        java.time.LocalDateTime localTime = java.time.LocalDateTime.parse(date, yyyyMMdd);
        CellValueReader<?> reader = cellValueReaderFactory.getReader(java.time.LocalDateTime.class, 0, CsvColumnDefinition.dateFormatDefinition("yyyyMMdd HH:mm:ss"), null);
        assertEquals(localTime, reader.read(date.toCharArray(), 0, date.length(), null));
    }


    @Test
    public void testJavaLocalDateTimeWithDateTimeFormatter() throws Exception {
        String date = "20150128 12:03:56";
        final DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");
        java.time.LocalDateTime localTime = java.time.LocalDateTime.parse(date, yyyyMMdd);
        CellValueReader<?> reader = cellValueReaderFactory.getReader(java.time.LocalDateTime.class, 0, CsvColumnDefinition.IDENTITY.add(new JavaDateTimeFormatterProperty(yyyyMMdd)), null);
        assertEquals(localTime, reader.read(date.toCharArray(), 0, date.length(), null));
    }


    @Test
    public void testJavaZonedDateTime() throws Exception {
        // fail in java 9, java 9 bug?
        String date = "20150128 12:03:56 PST";
        final DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss z");
        java.time.ZonedDateTime localTime = java.time.ZonedDateTime.parse(date, yyyyMMdd);
        CellValueReader<?> reader = cellValueReaderFactory.getReader(java.time.ZonedDateTime.class, 0, CsvColumnDefinition.dateFormatDefinition("yyyyMMdd HH:mm:ss z"), null);
        assertEquals(localTime, reader.read(date.toCharArray(), 0, date.length(), null));
    }

    @Test
    public void testJavaOffsetDateTime() throws Exception {
        String date = "20150128 12:03:56+01";
        final DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ssx").withZone(ZoneId.systemDefault());
        java.time.OffsetDateTime localTime = java.time.OffsetDateTime.parse(date, yyyyMMdd);
        CellValueReader<?> reader = cellValueReaderFactory.getReader(java.time.OffsetDateTime.class, 0, CsvColumnDefinition.dateFormatDefinition("yyyyMMdd HH:mm:ssx"), null);
        assertEquals(localTime, reader.read(date.toCharArray(), 0, date.length(), null));
    }

    @Test
    public void testJavaOffsetTime() throws Exception {
        String date = "20150128 12:03:56+01";
        final DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ssx").withZone(ZoneId.systemDefault());
        java.time.OffsetTime localTime = java.time.OffsetTime.parse(date, yyyyMMdd);
        CellValueReader<?> reader = cellValueReaderFactory.getReader(java.time.OffsetTime.class, 0, CsvColumnDefinition.dateFormatDefinition("yyyyMMdd HH:mm:ssx"), null);
        assertEquals(localTime, reader.read(date.toCharArray(), 0, date.length(), null));
    }

    @Test
    public void testJavaInstant() throws Exception {
        String date = "20150128 12:03:56";
        final DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss").withZone(ZoneId.systemDefault());
        java.time.Instant localTime = java.time.ZonedDateTime.parse(date, yyyyMMdd).toInstant();
        CellValueReader<?> reader = cellValueReaderFactory.getReader(java.time.Instant.class, 0, CsvColumnDefinition.dateFormatDefinition("yyyyMMdd HH:mm:ss"), null);
        assertEquals(localTime, reader.read(date.toCharArray(), 0, date.length(), null));
    }

    @Test
    public void testJavaYear() throws Exception {
        String date = "20150128";
        final DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneId.systemDefault());
        java.time.Year localTime = java.time.Year.parse(date, yyyyMMdd);
        CellValueReader<?> reader = cellValueReaderFactory.getReader(java.time.Year.class, 0, CsvColumnDefinition.dateFormatDefinition("yyyyMMdd"), null);
        assertEquals(localTime, reader.read(date.toCharArray(), 0, date.length(), null));
    }

    @Test
    public void testJavaYearMonth() throws Exception {
        String date = "20150128";
        final DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneId.systemDefault());
        java.time.YearMonth localTime = java.time.YearMonth.parse(date, yyyyMMdd);
        CellValueReader<?> reader = cellValueReaderFactory.getReader(java.time.YearMonth.class, 0, CsvColumnDefinition.dateFormatDefinition("yyyyMMdd"), null);
        assertEquals(localTime, reader.read(date.toCharArray(), 0, date.length(), null));
    }
    //IFJAVA8_END

}
