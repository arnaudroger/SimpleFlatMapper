package org.simpleflatmapper.jdbc.test.impl.getter;


import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.jdbc.SqlTypeColumnProperty;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.ResultSetGetterFactory;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.IntGetter;
import org.simpleflatmapper.util.UUIDHelper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;

//IFJAVA8_START
import java.time.*;
import org.simpleflatmapper.jdbc.property.time.ZoneOffsetProperty;
//IFJAVA8_END

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

public class ResultSetGetterFactoryTest {

	public static final FieldMapperColumnDefinition<JdbcColumnKey> IDENTITY = FieldMapperColumnDefinition.identity();
	ResultSetGetterFactory factory;
	ResultSet resultSet;

	@Before
	public void setUp() {
		factory =  ResultSetGetterFactory.INSTANCE;
		resultSet = mock(ResultSet.class);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInt() throws  Exception {
		when(resultSet.getInt(1)).thenReturn(13);
		IntGetter<ResultSet> intGetter = (IntGetter<ResultSet>) (factory.newGetter(Integer.class, key(Types.NUMERIC), IDENTITY.properties()));
		assertEquals(13, intGetter.getInt(resultSet));
		intGetter = (IntGetter<ResultSet>) (factory.newGetter(int.class, key(Types.NUMERIC), IDENTITY.properties()));
		assertEquals(13, intGetter.getInt(resultSet));
	}

	@Test
	public void testNString() throws Exception {
		when(resultSet.getNString(1)).thenReturn("value");
		assertEquals("value", factory.newGetter(String.class, key(Types.NCHAR), IDENTITY.properties()).get(resultSet));
		assertEquals("value", factory.newGetter(String.class, key(Types.NCLOB), IDENTITY.properties()).get(resultSet));
		assertEquals("value", factory.newGetter(String.class, key(Types.NVARCHAR), IDENTITY.properties()).get(resultSet));
	}

	@Test
	public void testBlob() throws Exception {
		Blob blob = mock(Blob.class);
		when(resultSet.getBlob(1)).thenReturn(blob);
        Getter<ResultSet, Object> getter = factory.newGetter(Blob.class, key(Types.BLOB), IDENTITY.properties());
        assertEquals(blob, getter.get(resultSet));
        assertEquals("BlobResultSetGetter{property=1}", getter.toString());
	}

	@Test
	public void testClob() throws Exception {
		Clob blob = mock(Clob.class);
		when(resultSet.getClob(1)).thenReturn(blob);
        Getter<ResultSet, Object> getter = factory.newGetter(Clob.class, key(Types.CLOB), IDENTITY.properties());
        assertEquals(blob, getter.get(resultSet));
        assertEquals("ClobResultSetGetter{property=1}", getter.toString());
	}

	@Test
	public void testReader() throws Exception {
		Reader blob = mock(Reader.class);
		when(resultSet.getCharacterStream(1)).thenReturn(blob);
        Getter<ResultSet, Object> getter = factory.newGetter(Reader.class, key(Types.CLOB), IDENTITY.properties());
        assertEquals(blob, getter.get(resultSet));
        assertEquals("ReaderResultSetGetter{property=1}", getter.toString());
	}

	@Test
	public void testNClob() throws Exception {
		NClob blob = mock(NClob.class);
		when(resultSet.getNClob(1)).thenReturn(blob);
        Getter<ResultSet, Object> getter = factory.newGetter(NClob.class, key(Types.NCLOB), IDENTITY.properties());
        assertEquals(blob, getter.get(resultSet));
        assertEquals("NClobResultSetGetter{property=1}", getter.toString());
	}

	@Test
	public void testNReader() throws Exception {
		Reader blob = mock(Reader.class);
		when(resultSet.getNCharacterStream(1)).thenReturn(blob);
        Getter<ResultSet, Object> getter = factory.newGetter(Reader.class, key(Types.NCLOB), IDENTITY.properties());
        assertEquals(blob, getter.get(resultSet));
        assertEquals("NReaderResultSetGetter{property=1}", getter.toString());
	}

	@Test
	public void testInputStream() throws Exception {
		InputStream inputStream = mock(InputStream.class);
		when(resultSet.getBinaryStream(1)).thenReturn(inputStream);
        Getter<ResultSet, Object> getter = factory.newGetter(InputStream.class, key(Types.BLOB), IDENTITY.properties());
        assertEquals(inputStream, getter.get(resultSet));
        assertEquals("InputStreamResultSetGetter{property=1}", getter.toString());
	}

	@Test
	public void testRef() throws Exception {
		Ref ref = mock(Ref.class);
		when(resultSet.getRef(1)).thenReturn(ref);
        Getter<ResultSet, Object> getter = factory.newGetter(Ref.class, key(Types.REF), IDENTITY.properties());
        assertEquals(ref, getter.get(resultSet));
        assertEquals("RefResultSetGetter{property=1}", getter.toString());
	}

	@Test
	public void testRowId() throws Exception {
		RowId rowId = mock(RowId.class);
		when(resultSet.getRowId(1)).thenReturn(rowId);
        Getter<ResultSet, Object> getter = factory.newGetter(RowId.class, key(Types.ROWID), IDENTITY.properties());
        assertEquals(rowId, getter.get(resultSet));
        assertEquals("RowIdResultSetGetter{property=1}", getter.toString());
	}

	@Test
	public void testSqlArray() throws Exception {
		Array array = mock(Array.class);
		when(resultSet.getArray(1)).thenReturn(array);
        Getter<ResultSet, Object> getter = factory.newGetter(Array.class, key(Types.ARRAY), IDENTITY.properties());
        assertEquals(array, getter.get(resultSet));
        assertEquals("SqlArrayResultSetGetter{property=1}", getter.toString());
	}

	@Test
	public void testSqlXml() throws Exception {
		SQLXML sqlxml = mock(SQLXML.class);
		when(resultSet.getSQLXML(1)).thenReturn(sqlxml);
        Getter<ResultSet, Object> getter = factory.newGetter(SQLXML.class, key(Types.SQLXML), IDENTITY.properties());
        assertEquals(sqlxml, getter.get(resultSet));
        assertEquals("SQLXMLResultSetGetter{property=1}", getter.toString());
	}

	@Test
	public void testUrl() throws Exception {
		URL url = new URL("http://url.net");
		when(resultSet.getURL(1)).thenReturn(url);
        Getter<ResultSet, Object> getter = factory.newGetter(URL.class, key(Types.DATALINK), IDENTITY.properties());
        assertEquals(url, getter.get(resultSet));
        assertEquals("UrlResultSetGetter{property=1}", getter.toString());
	}

	@Test
	public void testUrlFromString() throws Exception {
		URL url = new URL("http://url.net");
		when(resultSet.getString(1)).thenReturn("http://url.net");
        Getter<ResultSet, Object> getter = factory.newGetter(URL.class, key(Types.VARCHAR), IDENTITY.properties());
        assertEquals(url, getter.get(resultSet));
        assertEquals("UrlFromStringResultSetGetter{property=1}", getter.toString());
	}

	@Test
	public void testJavaUtilDateFromUndefined() throws Exception {
		java.util.Date date = new java.util.Date(13l);
		when(resultSet.getObject(1)).thenReturn(date);
        Getter<ResultSet, Object> getter = factory.newGetter(java.util.Date.class, key(JdbcColumnKey.UNDEFINED_TYPE), IDENTITY.properties());
        assertEquals(date, getter.get(resultSet));
		when(resultSet.getObject(1)).thenReturn(131l);
		assertEquals(new java.util.Date(131l), getter.get(resultSet));
        assertEquals("UndefinedDateResultSetGetter{property=1}", getter.toString());
	}

	@Test
	public void testTime() throws Exception {
		Time time = new Time(10000);
		when(resultSet.getTime(1)).thenReturn(time);
		assertEquals(time, factory.newGetter(Time.class, key(JdbcColumnKey.UNDEFINED_TYPE), IDENTITY.properties()).get(resultSet));
        Getter<ResultSet, Object> getter = factory.newGetter(java.util.Date.class, key(Types.TIME), IDENTITY.properties());
        assertEquals(time, getter.get(resultSet));
        assertEquals("TimeResultSetGetter{property=1}", getter.toString());
	}

	@Test
	public void testTimestamp() throws Exception {
		Timestamp time = new Timestamp(10000);
		when(resultSet.getTimestamp(1)).thenReturn(time);
		assertEquals(time, factory.newGetter(Timestamp.class, key(JdbcColumnKey.UNDEFINED_TYPE), IDENTITY.properties()).get(resultSet));
        Getter<ResultSet, Object> getter = factory.newGetter(java.util.Date.class, key(Types.TIMESTAMP), IDENTITY.properties());
        assertEquals(time, getter.get(resultSet));
        assertEquals("TimestampResultSetGetter{property=1}", getter.toString());
	}
	@Test
	public void testSqlDate() throws Exception {
		java.sql.Date time = new Date(10000);
		when(resultSet.getDate(1)).thenReturn(time);
		assertEquals(time, factory.newGetter(Date.class, key(JdbcColumnKey.UNDEFINED_TYPE), IDENTITY.properties()).get(resultSet));
        Getter<ResultSet, Object> getter = factory.newGetter(java.util.Date.class, key(Types.DATE), IDENTITY.properties());
        assertEquals(time, getter.get(resultSet));
        assertEquals("DateResultSetGetter{property=1}", getter.toString());
	}
	@Test
	public void testObject() throws Exception {
		Object object = new Object();
		when(resultSet.getObject(1)).thenReturn(object);
        Getter<ResultSet, Object> getter = factory.newGetter(Object.class, key(JdbcColumnKey.UNDEFINED_TYPE), IDENTITY.properties());
        assertEquals(object, getter.get(resultSet));
        assertEquals("ObjectResultSetGetter{property=1}", getter.toString());
	}

	@Test
	public void testBigDecimal() throws Exception {
		BigDecimal bg = new BigDecimal("3.14");
		when(resultSet.getBigDecimal(1)).thenReturn(bg);
		Getter<ResultSet, BigDecimal> getter = factory.newGetter(BigDecimal.class, key(Types.DECIMAL), IDENTITY.properties());
		assertEquals(bg, getter.get(resultSet));

		reset(resultSet);
		when(resultSet.getBigDecimal(1)).thenReturn(null);
		assertNull(getter.get(resultSet));


		reset(resultSet);
		getter = factory.newGetter(BigDecimal.class, key(Types.VARCHAR), IDENTITY.properties());
		when(resultSet.getString(1)).thenReturn("3.14");
		assertEquals(bg, getter.get(resultSet));

		reset(resultSet);
		when(resultSet.getString(1)).thenReturn(null);
		assertNull(getter.get(resultSet));

		reset(resultSet);
		getter = factory.newGetter(BigDecimal.class, key(Types.DECIMAL), IDENTITY.add(SqlTypeColumnProperty.of(Types.VARCHAR)).properties());
		when(resultSet.getString(1)).thenReturn("3.14");
		assertEquals(bg, getter.get(resultSet));
		
		reset(resultSet);
		when(resultSet.getString(1)).thenReturn(null);
		assertNull(getter.get(resultSet));

	}

	@Test
	public void testBigInteger() throws Exception {
		BigDecimal bg = new BigDecimal("314");
		when(resultSet.getBigDecimal(1)).thenReturn(bg);
		Getter<ResultSet, BigInteger> getter = factory.newGetter(BigInteger.class, key(Types.DECIMAL), IDENTITY.properties());
		assertEquals(bg.toBigInteger(), getter.get(resultSet));
		
		reset(resultSet);
		when(resultSet.getBigDecimal(1)).thenReturn(null);
		assertNull(getter.get(resultSet));
		
		reset(resultSet);
		getter = factory.newGetter(BigInteger.class, key(Types.VARCHAR), IDENTITY.properties());
		when(resultSet.getString(1)).thenReturn("314");
		assertEquals(bg.toBigInteger(), getter.get(resultSet));

		reset(resultSet);
		when(resultSet.getString(1)).thenReturn(null);
		assertNull(getter.get(resultSet));


		reset(resultSet);
		getter = factory.newGetter(BigInteger.class, key(Types.DECIMAL), IDENTITY.add(SqlTypeColumnProperty.of(Types.VARCHAR)).properties());
		when(resultSet.getString(1)).thenReturn("314");
		assertEquals(bg.toBigInteger(), getter.get(resultSet));
		
		reset(resultSet);
		when(resultSet.getString(1)).thenReturn(null);
		assertNull(getter.get(resultSet));

	}

	private JdbcColumnKey key(int type) {
		return new JdbcColumnKey("NA", 1, type);
	}


	@Test
	public void testCalendar() throws Exception {
		String date = "20150128";
		java.util.Date dd = new SimpleDateFormat("yyyyMMdd").parse(date);
		Calendar cal = Calendar.getInstance();
		cal.setTime(dd);

		when(resultSet.getTimestamp(1)).thenReturn(new Timestamp(dd.getTime()));

        Getter<ResultSet, Calendar> getter = factory.<Calendar>newGetter(Calendar.class, key(Types.TIMESTAMP), IDENTITY.properties());
        assertEquals(cal, getter.get(resultSet));
        assertEquals("CalendarResultSetGetter{dateGetter=TimestampResultSetGetter{property=1}}", getter.toString());
	}

	@Test
	public void testCalendarOnInvalidType() throws Exception {
		Getter<ResultSet, Calendar> getter = factory.<Calendar>newGetter(Calendar.class, key(Types.VARCHAR), IDENTITY.properties());
		assertNull(getter);
	}

	@Test
	public void testSQLData() throws Exception {
		Getter<ResultSet, SQLDataImpl> getter = factory.<SQLDataImpl>newGetter(SQLDataImpl.class, key(Types.JAVA_OBJECT), IDENTITY.properties());

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
	public void testJavaOffsetDateTime() throws Exception {
		final OffsetDateTime offsetDateTime = OffsetDateTime.now();

		when(resultSet.getObject(1)).thenReturn(offsetDateTime, (OffsetDateTime) null);

		Getter<ResultSet, java.time.OffsetDateTime> getter = factory.<java.time.OffsetDateTime>newGetter(java.time.OffsetDateTime.class, key(Types.TIMESTAMP_WITH_TIMEZONE));
		assertEquals(offsetDateTime, getter.get(resultSet));
		assertNull(getter.get(resultSet));
	}

	@Test
	public void testJavaOffsetTime() throws Exception {
		final OffsetTime offsetTime = OffsetTime.now();

		when(resultSet.getObject(1)).thenReturn(offsetTime, (OffsetTime) null);

		Getter<ResultSet, java.time.OffsetTime> getter = factory.<java.time.OffsetTime>newGetter(java.time.OffsetTime.class, key(Types.TIME_WITH_TIMEZONE));

		assertEquals(offsetTime, getter.get(resultSet));
		assertNull(getter.get(resultSet));
	}

	//IFJAVA8_END


	@Test
	public void testUUIDUndefinedType() throws Exception {
		UUID uuid = UUID.randomUUID();

		when(resultSet.getObject(1)).thenReturn(
				uuid.toString(),
				UUIDHelper.toBytes(uuid),
				new ByteArrayInputStream(UUIDHelper.toBytes(uuid)));
		final Getter<ResultSet, UUID> getter = factory.<UUID>newGetter(UUID.class, key(JdbcColumnKey.UNDEFINED_TYPE), IDENTITY.properties());

		assertEquals(uuid, getter.get(resultSet));
		assertEquals(uuid, getter.get(resultSet));
		assertEquals(uuid, getter.get(resultSet));
	}

	@Test
	public void testUUIDString() throws Exception {
		UUID uuid = UUID.randomUUID();

		when(resultSet.getString(1)).thenReturn(uuid.toString());
		final Getter<ResultSet, UUID> getter = factory.<UUID>newGetter(UUID.class, key(Types.VARCHAR), IDENTITY.properties());

		assertEquals(uuid, getter.get(resultSet));
	}

	@Test
	public void testUUIDBytes() throws Exception {
		UUID uuid = UUID.randomUUID();

		when(resultSet.getBytes(1)).thenReturn(UUIDHelper.toBytes(uuid));
		final Getter<ResultSet, UUID> getter = factory.<UUID>newGetter(UUID.class, key(Types.BINARY), IDENTITY.properties());

		assertEquals(uuid, getter.get(resultSet));
	}
}
