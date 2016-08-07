package org.simpleflatmapper.jdbc.impl.getter;


import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.ResultSetGetterFactory;
import org.simpleflatmapper.map.column.FieldMapperColumnDefinition;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.IntGetter;
import org.simpleflatmapper.util.UUIDHelper;

import java.io.ByteArrayInputStream;
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ResultSetGetterFactoryTest {

	public static final FieldMapperColumnDefinition<JdbcColumnKey> IDENTITY = FieldMapperColumnDefinition.identity();
	ResultSetGetterFactory factory;
	ResultSet resultSet;
	
	@Before
	public void setUp() {
		factory = new ResultSetGetterFactory();
		resultSet = mock(ResultSet.class);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInt() throws  Exception {
		when(resultSet.getInt(1)).thenReturn(13);
		IntGetter<ResultSet> intGetter = (IntGetter<ResultSet>) (factory.newGetter(Integer.class, key(Types.NUMERIC), IDENTITY));
		assertEquals(13, intGetter.getInt(resultSet));
		intGetter = (IntGetter<ResultSet>) (factory.newGetter(int.class, key(Types.NUMERIC), IDENTITY));
		assertEquals(13, intGetter.getInt(resultSet));
	}

	@Test
	public void testNString() throws Exception {
		when(resultSet.getNString(1)).thenReturn("value");
		assertEquals("value", factory.newGetter(String.class, key(Types.NCHAR), IDENTITY).get(resultSet));
		assertEquals("value", factory.newGetter(String.class, key(Types.NCLOB), IDENTITY).get(resultSet));
		assertEquals("value", factory.newGetter(String.class, key(Types.NVARCHAR), IDENTITY).get(resultSet));
	}
	
	@Test
	public void testEnumNString() throws Exception {
		when(resultSet.getNString(1)).thenReturn("type4");
		assertEquals(DbObject.Type.type4, factory.newGetter(DbObject.Type.class, key(Types.NCHAR), IDENTITY).get(resultSet));
		assertEquals(DbObject.Type.type4, factory.newGetter(DbObject.Type.class, key(Types.NCLOB), IDENTITY).get(resultSet));
		assertEquals(DbObject.Type.type4, factory.newGetter(DbObject.Type.class, key(Types.NVARCHAR), IDENTITY).get(resultSet));

		when(resultSet.getString(1)).thenReturn("type3");
		assertEquals(DbObject.Type.type3, factory.newGetter(DbObject.Type.class, key(Types.CHAR), IDENTITY).get(resultSet));
		assertEquals(DbObject.Type.type3, factory.newGetter(DbObject.Type.class, key(Types.CLOB), IDENTITY).get(resultSet));
		assertEquals(DbObject.Type.type3, factory.newGetter(DbObject.Type.class, key(Types.VARCHAR), IDENTITY).get(resultSet));
	}

	@Test
	public void testBlob() throws Exception {
		Blob blob = mock(Blob.class);
		when(resultSet.getBlob(1)).thenReturn(blob);
        Getter<ResultSet, Object> getter = factory.newGetter(Blob.class, key(Types.BLOB), IDENTITY);
        assertEquals(blob, getter.get(resultSet));
        assertEquals("BlobResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testClob() throws Exception {
		Clob blob = mock(Clob.class);
		when(resultSet.getClob(1)).thenReturn(blob);
        Getter<ResultSet, Object> getter = factory.newGetter(Clob.class, key(Types.CLOB), IDENTITY);
        assertEquals(blob, getter.get(resultSet));
        assertEquals("ClobResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testReader() throws Exception {
		Reader blob = mock(Reader.class);
		when(resultSet.getCharacterStream(1)).thenReturn(blob);
        Getter<ResultSet, Object> getter = factory.newGetter(Reader.class, key(Types.CLOB), IDENTITY);
        assertEquals(blob, getter.get(resultSet));
        assertEquals("ReaderResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testNClob() throws Exception {
		NClob blob = mock(NClob.class);
		when(resultSet.getNClob(1)).thenReturn(blob);
        Getter<ResultSet, Object> getter = factory.newGetter(NClob.class, key(Types.NCLOB), IDENTITY);
        assertEquals(blob, getter.get(resultSet));
        assertEquals("NClobResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testNReader() throws Exception {
		Reader blob = mock(Reader.class);
		when(resultSet.getNCharacterStream(1)).thenReturn(blob);
        Getter<ResultSet, Object> getter = factory.newGetter(Reader.class, key(Types.NCLOB), IDENTITY);
        assertEquals(blob, getter.get(resultSet));
        assertEquals("NReaderResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testInputStream() throws Exception {
		InputStream inputStream = mock(InputStream.class);
		when(resultSet.getBinaryStream(1)).thenReturn(inputStream);
        Getter<ResultSet, Object> getter = factory.newGetter(InputStream.class, key(Types.BLOB), IDENTITY);
        assertEquals(inputStream, getter.get(resultSet));
        assertEquals("InputStreamResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testRef() throws Exception {
		Ref ref = mock(Ref.class);
		when(resultSet.getRef(1)).thenReturn(ref);
        Getter<ResultSet, Object> getter = factory.newGetter(Ref.class, key(Types.REF), IDENTITY);
        assertEquals(ref, getter.get(resultSet));
        assertEquals("RefResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testRowId() throws Exception {
		RowId rowId = mock(RowId.class);
		when(resultSet.getRowId(1)).thenReturn(rowId);
        Getter<ResultSet, Object> getter = factory.newGetter(RowId.class, key(Types.ROWID), IDENTITY);
        assertEquals(rowId, getter.get(resultSet));
        assertEquals("RowIdResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testSqlArray() throws Exception {
		Array array = mock(Array.class);
		when(resultSet.getArray(1)).thenReturn(array);
        Getter<ResultSet, Object> getter = factory.newGetter(Array.class, key(Types.ARRAY), IDENTITY);
        assertEquals(array, getter.get(resultSet));
        assertEquals("SqlArrayResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testSqlXml() throws Exception {
		SQLXML sqlxml = mock(SQLXML.class);
		when(resultSet.getSQLXML(1)).thenReturn(sqlxml);
        Getter<ResultSet, Object> getter = factory.newGetter(SQLXML.class, key(Types.SQLXML), IDENTITY);
        assertEquals(sqlxml, getter.get(resultSet));
        assertEquals("SQLXMLResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testUrl() throws Exception {
		URL url = new URL("http://url.net");
		when(resultSet.getURL(1)).thenReturn(url);
        Getter<ResultSet, Object> getter = factory.newGetter(URL.class, key(Types.DATALINK), IDENTITY);
        assertEquals(url, getter.get(resultSet));
        assertEquals("UrlResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testUrlFromString() throws Exception {
		URL url = new URL("http://url.net");
		when(resultSet.getString(1)).thenReturn("http://url.net");
        Getter<ResultSet, Object> getter = factory.newGetter(URL.class, key(Types.VARCHAR), IDENTITY);
        assertEquals(url, getter.get(resultSet));
        assertEquals("UrlFromStringResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testJavaUtilDateFromUndefined() throws Exception {
		java.util.Date date = new java.util.Date(13l);
		when(resultSet.getObject(1)).thenReturn(date);
        Getter<ResultSet, Object> getter = factory.newGetter(java.util.Date.class, key(JdbcColumnKey.UNDEFINED_TYPE), IDENTITY);
        assertEquals(date, getter.get(resultSet));
		when(resultSet.getObject(1)).thenReturn(131l);
		assertEquals(new java.util.Date(131l), getter.get(resultSet));
        assertEquals("UndefinedDateResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testTime() throws Exception {
		Time time = new Time(10000);
		when(resultSet.getTime(1)).thenReturn(time);
		assertEquals(time, factory.newGetter(Time.class, key(JdbcColumnKey.UNDEFINED_TYPE), IDENTITY).get(resultSet));
        Getter<ResultSet, Object> getter = factory.newGetter(java.util.Date.class, key(Types.TIME), IDENTITY);
        assertEquals(time, getter.get(resultSet));
        assertEquals("TimeResultSetGetter{column=1}", getter.toString());
	}

	@Test
	public void testTimestamp() throws Exception {
		Timestamp time = new Timestamp(10000);
		when(resultSet.getTimestamp(1)).thenReturn(time);
		assertEquals(time, factory.newGetter(Timestamp.class, key(JdbcColumnKey.UNDEFINED_TYPE), IDENTITY).get(resultSet));
        Getter<ResultSet, Object> getter = factory.newGetter(java.util.Date.class, key(Types.TIMESTAMP), IDENTITY);
        assertEquals(time, getter.get(resultSet));
        assertEquals("TimestampResultSetGetter{column=1}", getter.toString());
	}
	@Test
	public void testSqlDate() throws Exception {
		java.sql.Date time = new Date(10000);
		when(resultSet.getDate(1)).thenReturn(time);
		assertEquals(time, factory.newGetter(Date.class, key(JdbcColumnKey.UNDEFINED_TYPE), IDENTITY).get(resultSet));
        Getter<ResultSet, Object> getter = factory.newGetter(java.util.Date.class, key(Types.DATE), IDENTITY);
        assertEquals(time, getter.get(resultSet));
        assertEquals("DateResultSetGetter{column=1}", getter.toString());
	}
	@Test
	public void testObject() throws Exception {
		Object object = new Object();
		when(resultSet.getObject(1)).thenReturn(object);
        Getter<ResultSet, Object> getter = factory.newGetter(Object.class, key(JdbcColumnKey.UNDEFINED_TYPE), IDENTITY);
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
        Getter<ResultSet, DateTime> getter = factory.<DateTime>newGetter(DateTime.class, key(Types.TIMESTAMP), IDENTITY);
        DateTime dt = getter.get(resultSet);
		assertTrue(new DateTime(cal).isEqual(dt));
        assertEquals("JodaDateTimeFromDateGetter{getter=TimestampResultSetGetter{column=1}}", getter.toString());
	}
	@Test
	public void testJodaLocalDate() throws Exception {
		Calendar cal = Calendar.getInstance();
		Date ts = new Date(cal.getTimeInMillis());
		when(resultSet.getDate(1)).thenReturn(ts);
        Getter<ResultSet, LocalDate> getter = factory.<LocalDate>newGetter(LocalDate.class, key(Types.DATE), IDENTITY);
        LocalDate dt = getter.get(resultSet);
		assertTrue(new LocalDate(cal).isEqual(dt));
        assertEquals("DateToJodaLocalDateConverter{getter=DateResultSetGetter{column=1}}", getter.toString());
	}
	@Test
	public void testJodaLocalDateTime() throws Exception {
		Calendar cal = Calendar.getInstance();
		Timestamp ts = new Timestamp(cal.getTimeInMillis());
		when(resultSet.getTimestamp(1)).thenReturn(ts);
        Getter<ResultSet, LocalDateTime> getter = factory.<LocalDateTime>newGetter(LocalDateTime.class, key(Types.TIMESTAMP), IDENTITY);
        LocalDateTime dt = getter.get(resultSet);
		assertTrue(new LocalDateTime(cal).isEqual(dt));
        assertEquals("DateToJodaLocalDateTimeConverter{getter=TimestampResultSetGetter{column=1}}", getter.toString());
	}
	@Test
	public void testJodaLocalTime() throws Exception {
		Calendar cal = Calendar.getInstance();
		Time ts = new Time(cal.getTimeInMillis());
		when(resultSet.getTime(1)).thenReturn(ts);
        Getter<ResultSet, LocalTime> getter = factory.<LocalTime>newGetter(LocalTime.class, key(Types.TIME), IDENTITY);
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

        Getter<ResultSet, Calendar> getter = factory.<Calendar>newGetter(Calendar.class, key(Types.TIMESTAMP), IDENTITY);
        assertEquals(cal, getter.get(resultSet));
        assertEquals("CalendarResultSetGetter{dateGetter=TimestampResultSetGetter{column=1}}", getter.toString());
	}

	@Test
	public void testCalendarOnInvalidType() throws Exception {
		Getter<ResultSet, Calendar> getter = factory.<Calendar>newGetter(Calendar.class, key(Types.VARCHAR), IDENTITY);
		assertNull(getter);
	}

	@Test
	public void testSQLData() throws Exception {
		Getter<ResultSet, SQLDataImpl> getter = factory.<SQLDataImpl>newGetter(SQLDataImpl.class, key(Types.JAVA_OBJECT), IDENTITY);

		SQLDataImpl object = new SQLDataImpl();
		when(resultSet.getObject(1)).thenReturn(object);

		assertEquals(object, getter.get(resultSet));
	}

	public static class SQLDataImpl implements SQLData {

		@Override
		public String getSQLTypeName() throws SQLException {
			return "MyName";
		}

		@Override
		public void readSQL(SQLInput stream, String typeName) throws SQLException {

		}

		@Override
		public void writeSQL(SQLOutput stream) throws SQLException {

		}
	}

	//IFJAVA8_START
	@Test
	public void testJavaLocalDate() throws Exception {
		Getter<ResultSet, java.time.LocalDate> getter = factory.<java.time.LocalDate>newGetter(java.time.LocalDate.class, key(Types.DATE), IDENTITY);


		Date ts = new Date(System.currentTimeMillis());
		final ZonedDateTime zonedDateTime = Instant.ofEpochMilli(ts.getTime()).atZone(ZoneId.systemDefault());
		final java.time.LocalDate expected = zonedDateTime.toLocalDate();

		when(resultSet.getObject(1))
				.thenReturn(ts, // sql
						zonedDateTime.toLocalDateTime(), // local datetime
						zonedDateTime.toLocalDateTime().toLocalDate(), // local date
						zonedDateTime, // zoned date time
						zonedDateTime.toOffsetDateTime() // offset time
						, null
				);

		assertEquals(expected, getter.get(resultSet));
		assertEquals(expected, getter.get(resultSet));

		assertEquals(expected, getter.get(resultSet));
		assertEquals(expected, getter.get(resultSet));

		assertEquals(expected, getter.get(resultSet));

		assertNull(getter.get(resultSet));

		assertEquals("ObjectToJavaLocalDateConverter{getter=ObjectResultSetGetter{column=1}}", getter.toString());
	}

	@Test
	public void testJavaLocalDateTime() throws Exception {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		final ZonedDateTime zonedDateTime = Instant.ofEpochMilli(ts.getTime()).atZone(ZoneId.systemDefault());
		final java.time.LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();

		when(resultSet.getObject(1)).thenReturn(ts,
				zonedDateTime.toLocalDateTime(), // local datetime
				zonedDateTime, // zoned date time
				zonedDateTime.toOffsetDateTime() // offset time
				, null
		);

		Getter<ResultSet, java.time.LocalDateTime> getter = factory.<java.time.LocalDateTime>newGetter(java.time.LocalDateTime.class, key(Types.TIMESTAMP), IDENTITY);

		assertEquals(localDateTime, getter.get(resultSet));
		assertEquals(localDateTime, getter.get(resultSet));
		assertEquals(localDateTime, getter.get(resultSet));
		assertEquals(localDateTime, getter.get(resultSet));
		assertNull(getter.get(resultSet));

		assertEquals("ObjectToJavaLocalDateTimeConverter{getter=ObjectResultSetGetter{column=1}}", getter.toString());
	}

	@Test
	public void testJavaLocalTime() throws Exception {
		Time ts = new Time(System.currentTimeMillis());
		final ZonedDateTime zonedDateTime = Instant.ofEpochMilli(ts.getTime()).atZone(ZoneId.systemDefault());
		final java.time.LocalTime localTime = zonedDateTime.toLocalTime();

		when(resultSet.getObject(1)).thenReturn(ts,
				zonedDateTime.toLocalDateTime(), // local datetime
				zonedDateTime.toLocalTime(),
				zonedDateTime, // zoned date time
				zonedDateTime.toOffsetDateTime(), // offset time
				zonedDateTime.toOffsetDateTime().toOffsetTime(), null
		);

		Getter<ResultSet, java.time.LocalTime> getter = factory.<java.time.LocalTime>newGetter(java.time.LocalTime.class, key(Types.TIME), IDENTITY);

		assertEquals(localTime, getter.get(resultSet));
		assertEquals(localTime, getter.get(resultSet));
		assertEquals(localTime, getter.get(resultSet));
		assertEquals(localTime, getter.get(resultSet));
		assertEquals(localTime, getter.get(resultSet));
		assertEquals(localTime, getter.get(resultSet));
		assertNull(getter.get(resultSet));


		assertEquals("ObjectToJavaLocalTimeConverter{getter=ObjectResultSetGetter{column=1}}", getter.toString());
	}

	@Test
	public void testJavaOffsetDateTime() throws Exception {
		Calendar cal = Calendar.getInstance();
		Timestamp ts = new Timestamp(cal.getTimeInMillis());
		final Instant instant = Instant.ofEpochMilli(ts.getTime());
		final OffsetDateTime offsetDateTime = instant.atOffset(ZoneId.systemDefault().getRules().getOffset(instant));

		when(resultSet.getObject(1)).thenReturn(ts,
				offsetDateTime.toLocalDateTime(),
				offsetDateTime.toLocalDate(),
				offsetDateTime.toZonedDateTime(),
				offsetDateTime, null);

		Getter<ResultSet, java.time.OffsetDateTime> getter = factory.<java.time.OffsetDateTime>newGetter(java.time.OffsetDateTime.class, key(Types.TIMESTAMP), IDENTITY);
		assertEquals(offsetDateTime, getter.get(resultSet));
		assertEquals(offsetDateTime, getter.get(resultSet));
		assertEquals(offsetDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0), getter.get(resultSet));
		assertEquals(offsetDateTime, getter.get(resultSet));
		assertEquals(offsetDateTime, getter.get(resultSet));
		assertNull(getter.get(resultSet));
		assertEquals("ObjectToJavaOffsetDateTimeConverter{getter=ObjectResultSetGetter{column=1}}", getter.toString());
	}

	@Test
	public void testJavaOffsetTime() throws Exception {
		Calendar cal = Calendar.getInstance();
		Timestamp ts = new Timestamp(cal.getTimeInMillis());

		final Instant instant = Instant.ofEpochMilli(ts.getTime());
		final OffsetDateTime offsetDateTime = instant.atOffset(ZoneId.systemDefault().getRules().getOffset(instant));
		final OffsetTime offsetTime = offsetDateTime.toOffsetTime();

		when(resultSet.getObject(1)).thenReturn(ts,
				offsetDateTime.toLocalDateTime(),
				offsetDateTime.toLocalTime(),
				offsetDateTime.toZonedDateTime(),
				offsetDateTime,
				offsetDateTime.toOffsetTime(), null);

		Getter<ResultSet, java.time.OffsetTime> getter = factory.<java.time.OffsetTime>newGetter(java.time.OffsetTime.class, key(Types.TIME), IDENTITY);

		assertEquals(offsetTime, getter.get(resultSet));
		assertEquals(offsetTime, getter.get(resultSet));
		assertEquals(offsetTime.toLocalTime().atOffset(ZoneId.systemDefault().getRules().getOffset(Instant.now())), getter.get(resultSet));
		assertEquals(offsetTime, getter.get(resultSet));
		assertEquals(offsetTime, getter.get(resultSet));
		assertEquals(offsetTime, getter.get(resultSet));
		assertNull(getter.get(resultSet));


		assertEquals("ObjectToJavaOffsetTimeConverter{getter=ObjectResultSetGetter{column=1}}", getter.toString());
	}

	@Test
	public void testJavaZonedDateTime() throws Exception {
		Calendar cal = Calendar.getInstance();
		Timestamp ts = new Timestamp(cal.getTimeInMillis());
		final Instant instant = Instant.ofEpochMilli(ts.getTime());
		final ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

		when(resultSet.getObject(1)).thenReturn(ts,
				zonedDateTime.toLocalDateTime(),
				zonedDateTime,
				zonedDateTime.toOffsetDateTime(), null);

		Getter<ResultSet, java.time.ZonedDateTime> getter = factory.<java.time.ZonedDateTime>newGetter(java.time.ZonedDateTime.class, key(Types.TIMESTAMP), IDENTITY);

		assertEquals(zonedDateTime, getter.get(resultSet));
		assertEquals(zonedDateTime, getter.get(resultSet));
		assertEquals(zonedDateTime, getter.get(resultSet));
		assertEquals(zonedDateTime, getter.get(resultSet));
		assertNull(getter.get(resultSet));


		assertEquals("ObjectToJavaZonedDateTimeConverter{getter=ObjectResultSetGetter{column=1}}", getter.toString());
	}

	@Test
	public void testJavaInstant() throws Exception {
		Calendar cal = Calendar.getInstance();
		Timestamp ts = new Timestamp(cal.getTimeInMillis());
		when(resultSet.getObject(1)).thenReturn(ts, cal.getTimeInMillis(), Instant.ofEpochMilli(cal.getTimeInMillis()).atZone(ZoneId.systemDefault()), null);
		Getter<ResultSet, java.time.Instant> getter = factory.<java.time.Instant>newGetter(java.time.Instant.class, key(Types.TIMESTAMP), IDENTITY);
		final Instant instant = Instant.ofEpochMilli(ts.getTime());
		assertEquals(instant, getter.get(resultSet));
		assertEquals(instant, getter.get(resultSet));
		assertEquals(instant, getter.get(resultSet));
		assertNull(getter.get(resultSet));

		assertEquals("ObjectToJavaInstantConverter{getter=ObjectResultSetGetter{column=1}}", getter.toString());
	}

	@Test
	public void testJavaYearMonth() throws Exception {
		Calendar cal = Calendar.getInstance();
		Date ts = new Date(cal.getTimeInMillis());
		when(resultSet.getObject(1)).thenReturn(ts, Instant.ofEpochMilli(cal.getTimeInMillis()).atZone(ZoneId.systemDefault()), ts.toLocalDate().getYear()  * 100  + ts.toLocalDate().getMonthValue(), null);		Getter<ResultSet, java.time.YearMonth> getter = factory.<java.time.YearMonth>newGetter(java.time.YearMonth.class, key(Types.TIMESTAMP), IDENTITY);
		final Instant instant = Instant.ofEpochMilli(ts.getTime());
		final ZonedDateTime dateTime = instant.atZone(ZoneId.systemDefault());
		assertEquals(YearMonth.from(dateTime), getter.get(resultSet));
		assertEquals(YearMonth.from(dateTime), getter.get(resultSet));
		assertEquals(YearMonth.from(dateTime), getter.get(resultSet));
		assertNull(getter.get(resultSet));

		assertEquals("ObjectToJavaYearMonthConverter{getter=ObjectResultSetGetter{column=1}}", getter.toString());
	}

	@Test
	public void testJavaYear() throws Exception {
		java.time.LocalDateTime ldt = java.time.LocalDateTime.of(2029, 1, 2, 1, 1, 1);

		when(resultSet.getObject(1)).thenReturn(2029, ldt, new Date(ldt.toInstant(ZoneOffset.ofHours(0)).toEpochMilli()), null);
		Getter<ResultSet, java.time.Year> getter = factory.<java.time.Year>newGetter(java.time.Year.class, key(Types.INTEGER), IDENTITY);
		assertEquals(Year.of(2029), getter.get(resultSet));
		assertEquals(Year.of(2029), getter.get(resultSet));
		assertEquals(Year.of(2029), getter.get(resultSet));
		assertNull(getter.get(resultSet));

		assertEquals("ObjectToJavaYearConverter{getter=ObjectResultSetGetter{column=1}}", getter.toString());
	}

	//IFJAVA8_END


	@Test
	public void testUUIDUndefinedType() throws Exception {
		UUID uuid = UUID.randomUUID();

		when(resultSet.getObject(1)).thenReturn(
				uuid.toString(),
				UUIDHelper.toBytes(uuid),
				new ByteArrayInputStream(UUIDHelper.toBytes(uuid)));
		final Getter<ResultSet, UUID> getter = factory.<UUID>newGetter(UUID.class, key(JdbcColumnKey.UNDEFINED_TYPE), IDENTITY);

		assertEquals(uuid, getter.get(resultSet));
		assertEquals(uuid, getter.get(resultSet));
		assertEquals(uuid, getter.get(resultSet));
	}

	@Test
	public void testUUIDString() throws Exception {
		UUID uuid = UUID.randomUUID();

		when(resultSet.getString(1)).thenReturn(uuid.toString());
		final Getter<ResultSet, UUID> getter = factory.<UUID>newGetter(UUID.class, key(Types.VARCHAR), IDENTITY);

		assertEquals(uuid, getter.get(resultSet));
	}

	@Test
	public void testUUIDBytes() throws Exception {
		UUID uuid = UUID.randomUUID();

		when(resultSet.getBytes(1)).thenReturn(UUIDHelper.toBytes(uuid));
		final Getter<ResultSet, UUID> getter = factory.<UUID>newGetter(UUID.class, key(Types.BINARY), IDENTITY);

		assertEquals(uuid, getter.get(resultSet));
	}
}
