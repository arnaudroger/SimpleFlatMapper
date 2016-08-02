package org.simpleflatmapper.test.beans;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class DbExtendedType {
	
	private byte[] bytes;
	private URL url;
	private Time time;
	private java.sql.Date date;
	private BigDecimal bigDecimal;
	private BigInteger bigInteger;
	private String[] stringArray;
	private List<String> stringList;
	
	public byte[] getBytes() {
		return bytes;
	}
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	public URL getUrl() {
		return url;
	}
	public void setUrl(URL url) {
		this.url = url;
	}
	public Time getTime() {
		return time;
	}
	public void setTime(Time time) {
		this.time = time;
	}
	public java.sql.Date getDate() {
		return date;
	}
	public void setDate(java.sql.Date date) {
		this.date = date;
	}
	public BigDecimal getBigDecimal() {
		return bigDecimal;
	}
	public void setBigDecimal(BigDecimal bigDecimal) {
		this.bigDecimal = bigDecimal;
	}
	public BigInteger getBigInteger() {
		return bigInteger;
	}
	public void setBigInteger(BigInteger bigInteger) {
		this.bigInteger = bigInteger;
	}
	public String[] getStringArray() {
		return stringArray;
	}
	public void setStringArray(String[] stringArray) {
		this.stringArray = stringArray;
	}
	public List<String> getStringList() {
		return stringList;
	}
	public void setStringList(List<String> stringList) {
		this.stringList = stringList;
	}


	@SuppressWarnings("deprecation")
	public static void assertDbExtended(DbExtendedType o) {
		assertArrayEquals(new byte[] { 'a', 'b', 'c' }, o.getBytes());
		assertEquals(new BigInteger("123"), o.getBigInteger());
		assertEquals(new BigDecimal("123.321").toString(), o.getBigDecimal().toString());
		assertEquals(new Time(7, 8, 9), o.getTime());
		assertEquals(new Date(114, 10, 2), o.getDate());
		assertArrayEquals(new String[] { "HOT", "COLD"}, o.getStringArray());
		assertEquals(Arrays.asList("COLD", "FREEZING"), o.getStringList());
	}
}
