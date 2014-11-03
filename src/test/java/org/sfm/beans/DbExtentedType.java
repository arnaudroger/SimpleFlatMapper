package org.sfm.beans;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Time;

public class DbExtentedType {
	
	private byte[] bytes;
	private URL url;
	private Time time;
	private java.sql.Date date;
	private BigDecimal bigDecimal;
	private BigInteger bigInteger;
	private String[] stringArray;
	
	
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
	
}
