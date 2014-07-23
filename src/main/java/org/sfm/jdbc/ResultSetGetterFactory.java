package org.sfm.jdbc;

import java.sql.ResultSet;
import java.util.Date;

import org.sfm.jdbc.getter.BooleanIndexedResultSetGetter;
import org.sfm.jdbc.getter.BooleanNamedResultSetGetter;
import org.sfm.jdbc.getter.ByteIndexedResultSetGetter;
import org.sfm.jdbc.getter.ByteNamedResultSetGetter;
import org.sfm.jdbc.getter.CharacterIndexedResultSetGetter;
import org.sfm.jdbc.getter.CharacterNamedResultSetGetter;
import org.sfm.jdbc.getter.DoubleIndexedResultSetGetter;
import org.sfm.jdbc.getter.DoubleNamedResultSetGetter;
import org.sfm.jdbc.getter.FloatIndexedResultSetGetter;
import org.sfm.jdbc.getter.FloatNamedResultSetGetter;
import org.sfm.jdbc.getter.IntIndexedResultSetGetter;
import org.sfm.jdbc.getter.IntNamedResultSetGetter;
import org.sfm.jdbc.getter.LongIndexedResultSetGetter;
import org.sfm.jdbc.getter.LongNamedResultSetGetter;
import org.sfm.jdbc.getter.ShortIndexedResultSetGetter;
import org.sfm.jdbc.getter.ShortNamedResultSetGetter;
import org.sfm.jdbc.getter.StringIndexedResultSetGetter;
import org.sfm.jdbc.getter.StringNamedResultSetGetter;
import org.sfm.jdbc.getter.TimestampIndexedResultSetGetter;
import org.sfm.jdbc.getter.TimestampNamedResultSetGetter;
import org.sfm.reflect.Getter;

public class ResultSetGetterFactory {
	public static Getter<ResultSet, ? extends Object> newGetter(
			Class<? extends Object> type, String column) {
		Getter<ResultSet, ? extends Object> getter = null;
		if (type.isAssignableFrom(String.class)) {
			getter = new StringNamedResultSetGetter(column);
		} else if (type.isAssignableFrom(Date.class)) {
			getter = new TimestampNamedResultSetGetter(column);
		} else if (type.equals(Boolean.class)) {
			getter = new BooleanNamedResultSetGetter(column);
		} else if (type.equals(Integer.class)) {
			getter = new IntNamedResultSetGetter(column);
		} else if (type.equals(Long.class)) {
			getter = new LongNamedResultSetGetter(column);
		} else if (type.equals(Float.class)) {
			getter = new FloatNamedResultSetGetter(column);
		} else if (type.equals(Double.class)) {
			getter = new DoubleNamedResultSetGetter(column);
		} else if (type.equals(Byte.class)) {
			getter = new ByteNamedResultSetGetter(column);
		} else if (type.equals(Character.class)) {
			getter = new CharacterNamedResultSetGetter(column);
		} else if (type.equals(Short.class)) {
			getter = new ShortNamedResultSetGetter(column);
		}
		return getter;
	}
	
	public static Getter<ResultSet, ? extends Object> newGetter(
			Class<? extends Object> type, int column) {
		Getter<ResultSet, ? extends Object> getter = null;
		if (type.isAssignableFrom(String.class)) {
			getter = new StringIndexedResultSetGetter(column);
		} else if (type.isAssignableFrom(Date.class)) {
			getter = new TimestampIndexedResultSetGetter(column);
		} else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
			getter = new BooleanIndexedResultSetGetter(column);
		} else if (type.equals(Integer.class) || type.equals(int.class)) {
			getter = new IntIndexedResultSetGetter(column);
		} else if (type.equals(Long.class) || type.equals(long.class)) {
			getter = new LongIndexedResultSetGetter(column);
		} else if (type.equals(Float.class) || type.equals(float.class)) {
			getter = new FloatIndexedResultSetGetter(column);
		} else if (type.equals(Double.class) || type.equals(double.class)) {
			getter = new DoubleIndexedResultSetGetter(column);
		} else if (type.equals(Byte.class) || type.equals(byte.class)) {
			getter = new ByteIndexedResultSetGetter(column);
		} else if (type.equals(Character.class) || type.equals(char.class)) {
			getter = new CharacterIndexedResultSetGetter(column);
		} else if (type.equals(Short.class) || type.equals(short.class)) {
			getter = new ShortIndexedResultSetGetter(column);
		} 
		return getter;
	}
}
