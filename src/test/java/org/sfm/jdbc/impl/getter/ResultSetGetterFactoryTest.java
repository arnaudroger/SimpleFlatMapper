package org.sfm.jdbc.impl.getter;


import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.reflect.meta.ObjectClassMeta;

import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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
		assertEquals(blob, factory.newGetter(Blob.class, key(Types.BLOB)).get(resultSet));
	}

	@Test
	public void testClob() throws Exception {
		Clob blob = mock(Clob.class);
		when(resultSet.getClob(1)).thenReturn(blob);
		assertEquals(blob, factory.newGetter(Clob.class, key(Types.CLOB)).get(resultSet));
	}

	@Test
	public void testReader() throws Exception {
		Reader blob = mock(Reader.class);
		when(resultSet.getCharacterStream(1)).thenReturn(blob);
		assertEquals(blob, factory.newGetter(Reader.class, key(Types.CLOB)).get(resultSet));
	}

	@Test
	public void testNClob() throws Exception {
		NClob blob = mock(NClob.class);
		when(resultSet.getNClob(1)).thenReturn(blob);
		assertEquals(blob, factory.newGetter(NClob.class, key(Types.NCLOB)).get(resultSet));
	}

	@Test
	public void testNReader() throws Exception {
		Reader blob = mock(Reader.class);
		when(resultSet.getNCharacterStream(1)).thenReturn(blob);
		assertEquals(blob, factory.newGetter(Reader.class, key(Types.NCLOB)).get(resultSet));
	}

	@Test
	public void testInputStream() throws Exception {
		InputStream inputStream = mock(InputStream.class);
		when(resultSet.getBinaryStream(1)).thenReturn(inputStream);
		assertEquals(inputStream, factory.newGetter(InputStream.class, key(Types.BLOB)).get(resultSet));
	}

	@Test
	public void testRef() throws Exception {
		Ref ref = mock(Ref.class);
		when(resultSet.getRef(1)).thenReturn(ref);
		assertEquals(ref, factory.newGetter(Ref.class, key(Types.REF)).get(resultSet));
	}

	@Test
	public void testRowId() throws Exception {
		RowId rowId = mock(RowId.class);
		when(resultSet.getRowId(1)).thenReturn(rowId);
		assertEquals(rowId, factory.newGetter(RowId.class, key(Types.ROWID)).get(resultSet));
	}

	@Test
	public void testSqlArray() throws Exception {
		Array array = mock(Array.class);
		when(resultSet.getArray(1)).thenReturn(array);
		assertEquals(array, factory.newGetter(Array.class, key(Types.ARRAY)).get(resultSet));
	}

	@Test
	public void testSqlXml() throws Exception {
		SQLXML sqlxml = mock(SQLXML.class);
		when(resultSet.getSQLXML(1)).thenReturn(sqlxml);
		assertEquals(sqlxml, factory.newGetter(SQLXML.class, key(Types.SQLXML)).get(resultSet));
	}

	@Test
	public void testUrl() throws Exception {
		URL url = new URL("http://url.net");
		when(resultSet.getURL(1)).thenReturn(url);
		assertEquals(url, factory.newGetter(URL.class, key(Types.DATALINK)).get(resultSet));
	}

	@Test
	public void testUrlFromString() throws Exception {
		URL url = new URL("http://url.net");
		when(resultSet.getString(1)).thenReturn("http://url.net");
		assertEquals(url, factory.newGetter(URL.class, key(Types.VARCHAR)).get(resultSet));
	}

	@Test
	public void testJavaUtilDateFromUdefined() throws Exception {
		java.util.Date date = new java.util.Date(13l);
		when(resultSet.getObject(1)).thenReturn(date);
		assertEquals(date, factory.newGetter(java.util.Date.class, key(JdbcColumnKey.UNDEFINED_TYPE)).get(resultSet));
		when(resultSet.getObject(1)).thenReturn(131l);
		assertEquals(new java.util.Date(131l), factory.newGetter(java.util.Date.class, key(JdbcColumnKey.UNDEFINED_TYPE)).get(resultSet));
	}

	@Test
	public void testTime() throws Exception {
		Time time = new Time(10000);
		when(resultSet.getTime(1)).thenReturn(time);
		assertEquals(time, factory.newGetter(Time.class, key(JdbcColumnKey.UNDEFINED_TYPE)).get(resultSet));
		assertEquals(time, factory.newGetter(java.util.Date.class, key(Types.TIME)).get(resultSet));
	}

	@Test
	public void testTimestamp() throws Exception {
		Timestamp time = new Timestamp(10000);
		when(resultSet.getTimestamp(1)).thenReturn(time);
		assertEquals(time, factory.newGetter(Timestamp.class, key(JdbcColumnKey.UNDEFINED_TYPE)).get(resultSet));
		assertEquals(time, factory.newGetter(java.util.Date.class, key(Types.TIMESTAMP)).get(resultSet));
	}
	@Test
	public void testSqlDate() throws Exception {
		java.sql.Date time = new Date(10000);
		when(resultSet.getDate(1)).thenReturn(time);
		assertEquals(time, factory.newGetter(Date.class, key(JdbcColumnKey.UNDEFINED_TYPE)).get(resultSet));
		assertEquals(time, factory.newGetter(java.util.Date.class, key(Types.DATE)).get(resultSet));
	}
	@Test
	public void testObject() throws Exception {
		Object object = new Object();
		when(resultSet.getObject(1)).thenReturn(object);
		assertEquals(object, factory.newGetter(Object.class, key(JdbcColumnKey.UNDEFINED_TYPE)).get(resultSet));
	}

	private JdbcColumnKey key(int type) {
		return new JdbcColumnKey("NA", 1, type);
	}


	@Test
	public void testJodaDateTime() throws Exception {
		Calendar cal = Calendar.getInstance();
		Timestamp ts = new Timestamp(cal.getTimeInMillis());
		when(resultSet.getTimestamp(1)).thenReturn(ts);
		DateTime dt = factory.<DateTime>newGetter(DateTime.class, key(Types.TIMESTAMP)).get(resultSet);
		assertTrue(new DateTime(cal).isEqual(dt));
	}
	@Test
	public void testJodaLocalDate() throws Exception {
		Calendar cal = Calendar.getInstance();
		Date ts = new Date(cal.getTimeInMillis());
		when(resultSet.getDate(1)).thenReturn(ts);
		LocalDate dt = factory.<LocalDate>newGetter(LocalDate.class, key(Types.DATE)).get(resultSet);
		assertTrue(new LocalDate(cal).isEqual(dt));
	}
	@Test
	public void testJodaLocalDateTime() throws Exception {
		Calendar cal = Calendar.getInstance();
		Timestamp ts = new Timestamp(cal.getTimeInMillis());
		when(resultSet.getTimestamp(1)).thenReturn(ts);
		LocalDateTime dt = factory.<LocalDateTime>newGetter(LocalDateTime.class, key(Types.TIMESTAMP)).get(resultSet);
		assertTrue(new LocalDateTime(cal).isEqual(dt));
	}
	@Test
	public void testJodaLocalTime() throws Exception {
		Calendar cal = Calendar.getInstance();
		Time ts = new Time(cal.getTimeInMillis());
		when(resultSet.getTime(1)).thenReturn(ts);
		LocalTime dt = factory.<LocalTime>newGetter(LocalTime.class, key(Types.TIME)).get(resultSet);
		assertTrue(new LocalTime(cal).isEqual(dt));
	}
}
