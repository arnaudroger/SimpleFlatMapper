package org.sfm.jdbc.impl.getter;


import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.reflect.Getter;

import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;

//IFJAVA8_START
import java.time.*;
//IFJAVA8_END

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ResultSetGetterFactoryTest {

	ResultSetGetterFactory factory;
	ResultSet resultSet;
	
	@Before
	public void setUp() {
		factory = new ResultSetGetterFactory();
		resultSet = mock(ResultSet.class);
	}
	
	@Test
	public void testNString() throws Exception {
		when(resultSet.getNString(1)).thenReturn("value");
		assertEquals("value", factory.newGetter(String.class, key(Types.NCHAR)).get(resultSet));
		assertEquals("value", factory.newGetter(String.class, key(Types.NCLOB)).get(resultSet));
		assertEquals("value", factory.newGetter(String.class, key(Types.NVARCHAR)).get(resultSet));
	}
	
	@Test
	public void testEnumNString() throws Exception {
		when(resultSet.getNString(1)).thenReturn("type4");
		assertEquals(DbObject.Type.type4, factory.newGetter(DbObject.Type.class, key(Types.NCHAR)).get(resultSet));
		assertEquals(DbObject.Type.type4, factory.newGetter(DbObject.Type.class, key(Types.NCLOB)).get(resultSet));
		assertEquals(DbObject.Type.type4, factory.newGetter(DbObject.Type.class, key(Types.NVARCHAR)).get(resultSet));

		when(resultSet.getString(1)).thenReturn("type3");
		assertEquals(DbObject.Type.type3, factory.newGetter(DbObject.Type.class, key(Types.CHAR)).get(resultSet));
		assertEquals(DbObject.Type.type3, factory.newGetter(DbObject.Type.class, key(Types.CLOB)).get(resultSet));
		assertEquals(DbObject.Type.type3, factory.newGetter(DbObject.Type.class, key(Types.VARCHAR)).get(resultSet));
	}

	@Test
	public void testBlob() throws Exception {
		Blob blob = mock(Blob.class);
		when(resultSet.getBlob(1)).thenReturn(blob);
        Getter<ResultSet, Object> getter = factory.newGetter(Blob.class, key(Types.BLOB));
        assertEquals(blob, getter.get(resultSet));
        assertEquals("BlobResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testClob() throws Exception {
		Clob blob = mock(Clob.class);
		when(resultSet.getClob(1)).thenReturn(blob);
        Getter<ResultSet, Object> getter = factory.newGetter(Clob.class, key(Types.CLOB));
        assertEquals(blob, getter.get(resultSet));
        assertEquals("ClobResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testReader() throws Exception {
		Reader blob = mock(Reader.class);
		when(resultSet.getCharacterStream(1)).thenReturn(blob);
        Getter<ResultSet, Object> getter = factory.newGetter(Reader.class, key(Types.CLOB));
        assertEquals(blob, getter.get(resultSet));
        assertEquals("ReaderResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testNClob() throws Exception {
		NClob blob = mock(NClob.class);
		when(resultSet.getNClob(1)).thenReturn(blob);
        Getter<ResultSet, Object> getter = factory.newGetter(NClob.class, key(Types.NCLOB));
        assertEquals(blob, getter.get(resultSet));
        assertEquals("NClobResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testNReader() throws Exception {
		Reader blob = mock(Reader.class);
		when(resultSet.getNCharacterStream(1)).thenReturn(blob);
        Getter<ResultSet, Object> getter = factory.newGetter(Reader.class, key(Types.NCLOB));
        assertEquals(blob, getter.get(resultSet));
        assertEquals("NReaderResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testInputStream() throws Exception {
		InputStream inputStream = mock(InputStream.class);
		when(resultSet.getBinaryStream(1)).thenReturn(inputStream);
        Getter<ResultSet, Object> getter = factory.newGetter(InputStream.class, key(Types.BLOB));
        assertEquals(inputStream, getter.get(resultSet));
        assertEquals("InputStreamResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testRef() throws Exception {
		Ref ref = mock(Ref.class);
		when(resultSet.getRef(1)).thenReturn(ref);
        Getter<ResultSet, Object> getter = factory.newGetter(Ref.class, key(Types.REF));
        assertEquals(ref, getter.get(resultSet));
        assertEquals("RefResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testRowId() throws Exception {
		RowId rowId = mock(RowId.class);
		when(resultSet.getRowId(1)).thenReturn(rowId);
        Getter<ResultSet, Object> getter = factory.newGetter(RowId.class, key(Types.ROWID));
        assertEquals(rowId, getter.get(resultSet));
        assertEquals("RowIdResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testSqlArray() throws Exception {
		Array array = mock(Array.class);
		when(resultSet.getArray(1)).thenReturn(array);
        Getter<ResultSet, Object> getter = factory.newGetter(Array.class, key(Types.ARRAY));
        assertEquals(array, getter.get(resultSet));
        assertEquals("SqlArrayResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testSqlXml() throws Exception {
		SQLXML sqlxml = mock(SQLXML.class);
		when(resultSet.getSQLXML(1)).thenReturn(sqlxml);
        Getter<ResultSet, Object> getter = factory.newGetter(SQLXML.class, key(Types.SQLXML));
        assertEquals(sqlxml, getter.get(resultSet));
        assertEquals("SQLXMLResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testUrl() throws Exception {
		URL url = new URL("http://url.net");
		when(resultSet.getURL(1)).thenReturn(url);
        Getter<ResultSet, Object> getter = factory.newGetter(URL.class, key(Types.DATALINK));
        assertEquals(url, getter.get(resultSet));
        assertEquals("UrlResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testUrlFromString() throws Exception {
		URL url = new URL("http://url.net");
		when(resultSet.getString(1)).thenReturn("http://url.net");
        Getter<ResultSet, Object> getter = factory.newGetter(URL.class, key(Types.VARCHAR));
        assertEquals(url, getter.get(resultSet));
        assertEquals("UrlFromStringResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testJavaUtilDateFromUndefined() throws Exception {
		java.util.Date date = new java.util.Date(13l);
		when(resultSet.getObject(1)).thenReturn(date);
        Getter<ResultSet, Object> getter = factory.newGetter(java.util.Date.class, key(JdbcColumnKey.UNDEFINED_TYPE));
        assertEquals(date, getter.get(resultSet));
		when(resultSet.getObject(1)).thenReturn(131l);
		assertEquals(new java.util.Date(131l), getter.get(resultSet));
        assertEquals("UndefinedDateResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testTime() throws Exception {
		Time time = new Time(10000);
		when(resultSet.getTime(1)).thenReturn(time);
		assertEquals(time, factory.newGetter(Time.class, key(JdbcColumnKey.UNDEFINED_TYPE)).get(resultSet));
        Getter<ResultSet, Object> getter = factory.newGetter(java.util.Date.class, key(Types.TIME));
        assertEquals(time, getter.get(resultSet));
        assertEquals("TimeResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testTimestamp() throws Exception {
		Timestamp time = new Timestamp(10000);
		when(resultSet.getTimestamp(1)).thenReturn(time);
		assertEquals(time, factory.newGetter(Timestamp.class, key(JdbcColumnKey.UNDEFINED_TYPE)).get(resultSet));
        Getter<ResultSet, Object> getter = factory.newGetter(java.util.Date.class, key(Types.TIMESTAMP));
        assertEquals(time, getter.get(resultSet));
        assertEquals("TimestampResultSetGetter{column=1}", getter.toString());
	}
	@Test
	public void testSqlDate() throws Exception {
		java.sql.Date time = new Date(10000);
		when(resultSet.getDate(1)).thenReturn(time);
		assertEquals(time, factory.newGetter(Date.class, key(JdbcColumnKey.UNDEFINED_TYPE)).get(resultSet));
        Getter<ResultSet, Object> getter = factory.newGetter(java.util.Date.class, key(Types.DATE));
        assertEquals(time, getter.get(resultSet));
        assertEquals("DateResultSetGetter{column=1}", getter.toString());
	}
	@Test
	public void testObject() throws Exception {
		Object object = new Object();
		when(resultSet.getObject(1)).thenReturn(object);
        Getter<ResultSet, Object> getter = factory.newGetter(Object.class, key(JdbcColumnKey.UNDEFINED_TYPE));
        assertEquals(object, getter.get(resultSet));
        assertEquals("ObjectResultSetGetter{column=1}", getter.toString());
	}

	private JdbcColumnKey key(int type) {
		return new JdbcColumnKey("NA", 1, type);
	}


	@Test
	public void testJodaDateTime() throws Exception {
		Calendar cal = Calendar.getInstance();
		Timestamp ts = new Timestamp(cal.getTimeInMillis());
		when(resultSet.getTimestamp(1)).thenReturn(ts);
        Getter<ResultSet, DateTime> getter = factory.<DateTime>newGetter(DateTime.class, key(Types.TIMESTAMP));
        DateTime dt = getter.get(resultSet);
		assertTrue(new DateTime(cal).isEqual(dt));
        assertEquals("JodaDateTimeResultSetGetter{getter=TimestampResultSetGetter{column=1}}", getter.toString());
	}
	@Test
	public void testJodaLocalDate() throws Exception {
		Calendar cal = Calendar.getInstance();
		Date ts = new Date(cal.getTimeInMillis());
		when(resultSet.getDate(1)).thenReturn(ts);
        Getter<ResultSet, LocalDate> getter = factory.<LocalDate>newGetter(LocalDate.class, key(Types.DATE));
        LocalDate dt = getter.get(resultSet);
		assertTrue(new LocalDate(cal).isEqual(dt));
        assertEquals("JodaLocalDateResultSetGetter{getter=DateResultSetGetter{column=1}}", getter.toString());
	}
	@Test
	public void testJodaLocalDateTime() throws Exception {
		Calendar cal = Calendar.getInstance();
		Timestamp ts = new Timestamp(cal.getTimeInMillis());
		when(resultSet.getTimestamp(1)).thenReturn(ts);
        Getter<ResultSet, LocalDateTime> getter = factory.<LocalDateTime>newGetter(LocalDateTime.class, key(Types.TIMESTAMP));
        LocalDateTime dt = getter.get(resultSet);
		assertTrue(new LocalDateTime(cal).isEqual(dt));
        assertEquals("JodaLocalDateTimeResultSetGetter{getter=TimestampResultSetGetter{column=1}}", getter.toString());
	}
	@Test
	public void testJodaLocalTime() throws Exception {
		Calendar cal = Calendar.getInstance();
		Time ts = new Time(cal.getTimeInMillis());
		when(resultSet.getTime(1)).thenReturn(ts);
        Getter<ResultSet, LocalTime> getter = factory.<LocalTime>newGetter(LocalTime.class, key(Types.TIME));
        LocalTime dt = getter.get(resultSet);
		assertTrue(new LocalTime(cal).isEqual(dt));
        assertEquals("JodaLocalTimeResultSetGetter{getter=TimeResultSetGetter{column=1}}", getter.toString());
	}

	@Test
	public void testCalendar() throws Exception {
		String date = "20150128";
		java.util.Date dd = new SimpleDateFormat("yyyyMMdd").parse(date);
		Calendar cal = Calendar.getInstance();
		cal.setTime(dd);

		when(resultSet.getTimestamp(1)).thenReturn(new Timestamp(dd.getTime()));

        Getter<ResultSet, Calendar> getter = factory.<Calendar>newGetter(Calendar.class, key(Types.TIMESTAMP));
        assertEquals(cal, getter.get(resultSet));
        assertEquals("CalendarResultSetGetter{dateGetter=TimestampResultSetGetter{column=1}}", getter.toString());
	}


	//IFJAVA8_START
	@Test
	public void testJavaLocalDate() throws Exception {
		Calendar cal = Calendar.getInstance();
		Date ts = new Date(cal.getTimeInMillis());
		when(resultSet.getObject(1)).thenReturn(ts);
		Getter<ResultSet, java.time.LocalDate> getter = factory.<java.time.LocalDate>newGetter(java.time.LocalDate.class, key(Types.DATE));
		java.time.LocalDate dt = getter.get(resultSet);
		assertEquals(Instant.ofEpochMilli(ts.getTime()).atZone(ZoneId.systemDefault()).toLocalDate(), dt);
		assertEquals("JavaLocalDateResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testJavaLocalDateTime() throws Exception {
		Calendar cal = Calendar.getInstance();
		Timestamp ts = new Timestamp(cal.getTimeInMillis());
		when(resultSet.getObject(1)).thenReturn(ts);
		Getter<ResultSet, java.time.LocalDateTime> getter = factory.<java.time.LocalDateTime>newGetter(java.time.LocalDateTime.class, key(Types.TIMESTAMP));
		java.time.LocalDateTime dt = getter.get(resultSet);
		assertEquals(Instant.ofEpochMilli(ts.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime(), dt);
		assertEquals("JavaLocalDateTimeResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testJavaLocalTime() throws Exception {
		Calendar cal = Calendar.getInstance();
		Time ts = new Time(cal.getTimeInMillis());
		when(resultSet.getObject(1)).thenReturn(ts);
		Getter<ResultSet, java.time.LocalTime> getter = factory.<java.time.LocalTime>newGetter(java.time.LocalTime.class, key(Types.TIME));
		java.time.LocalTime dt = getter.get(resultSet);
		assertEquals(Instant.ofEpochMilli(ts.getTime()).atZone(ZoneId.systemDefault()).toLocalTime(), dt);
		assertEquals("JavaLocalTimeResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testJavaOffsetDateTime() throws Exception {
		Calendar cal = Calendar.getInstance();
		Timestamp ts = new Timestamp(cal.getTimeInMillis());
		when(resultSet.getObject(1)).thenReturn(ts);
		Getter<ResultSet, java.time.OffsetDateTime> getter = factory.<java.time.OffsetDateTime>newGetter(java.time.OffsetDateTime.class, key(Types.TIMESTAMP));
		java.time.OffsetDateTime dt = getter.get(resultSet);
		final Instant instant = Instant.ofEpochMilli(ts.getTime());
		assertEquals(instant.atOffset(ZoneId.systemDefault().getRules().getOffset(instant)), dt);
		assertEquals("JavaOffsetDateTimeResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testJavaOffsetTime() throws Exception {
		Calendar cal = Calendar.getInstance();
		Timestamp ts = new Timestamp(cal.getTimeInMillis());
		when(resultSet.getObject(1)).thenReturn(ts);
		Getter<ResultSet, java.time.OffsetTime> getter = factory.<java.time.OffsetTime>newGetter(java.time.OffsetTime.class, key(Types.TIME));
		java.time.OffsetTime dt = getter.get(resultSet);
		final Instant instant = Instant.ofEpochMilli(ts.getTime());
		assertEquals(instant.atOffset(ZoneId.systemDefault().getRules().getOffset(instant)).toOffsetTime(), dt);
		assertEquals("JavaOffsetTimeResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testJavaZonedDateTime() throws Exception {
		Calendar cal = Calendar.getInstance();
		Timestamp ts = new Timestamp(cal.getTimeInMillis());
		when(resultSet.getObject(1)).thenReturn(ts);
		Getter<ResultSet, java.time.ZonedDateTime> getter = factory.<java.time.ZonedDateTime>newGetter(java.time.ZonedDateTime.class, key(Types.TIMESTAMP));
		java.time.ZonedDateTime dt = getter.get(resultSet);
		final Instant instant = Instant.ofEpochMilli(ts.getTime());
		assertEquals(instant.atZone(ZoneId.systemDefault()), dt);
		assertEquals("JavaZonedDateTimeResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testJavaInstant() throws Exception {
		Calendar cal = Calendar.getInstance();
		Timestamp ts = new Timestamp(cal.getTimeInMillis());
		when(resultSet.getObject(1)).thenReturn(ts);
		Getter<ResultSet, java.time.Instant> getter = factory.<java.time.Instant>newGetter(java.time.Instant.class, key(Types.TIMESTAMP));
		java.time.Instant dt = getter.get(resultSet);
		final Instant instant = Instant.ofEpochMilli(ts.getTime());
		assertEquals(instant, dt);
		assertEquals("JavaInstantResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testJavaYearMonth() throws Exception {
		Calendar cal = Calendar.getInstance();
		Date ts = new Date(cal.getTimeInMillis());
		when(resultSet.getObject(1)).thenReturn(ts);
		Getter<ResultSet, java.time.YearMonth> getter = factory.<java.time.YearMonth>newGetter(java.time.YearMonth.class, key(Types.TIMESTAMP));
		java.time.YearMonth dt = getter.get(resultSet);
		final Instant instant = Instant.ofEpochMilli(ts.getTime());
		final ZonedDateTime dateTime = instant.atZone(ZoneId.systemDefault());
		assertEquals(YearMonth.from(dateTime), dt);
		assertEquals("JavaYearMonthResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testJavaYear() throws Exception {
		when(resultSet.getObject(1)).thenReturn(new Integer(2029));
		Getter<ResultSet, java.time.Year> getter = factory.<java.time.Year>newGetter(java.time.Year.class, key(Types.INTEGER));
		java.time.Year dt = getter.get(resultSet);
		assertEquals(Year.of(2029), dt);
		assertEquals("JavaYearResultSetGetter{column=1}", getter.toString());
	}


	//IFJAVA8_END
}
