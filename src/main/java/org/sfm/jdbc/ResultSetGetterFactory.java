package org.sfm.jdbc;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.Date;

import org.sfm.jdbc.getter.BooleanIndexedResultSetGetter;
import org.sfm.jdbc.getter.BooleanNamedResultSetGetter;
import org.sfm.jdbc.getter.ByteIndexedResultSetGetter;
import org.sfm.jdbc.getter.ByteNamedResultSetGetter;
import org.sfm.jdbc.getter.CharacterIndexedResultSetGetter;
import org.sfm.jdbc.getter.CharacterNamedResultSetGetter;
import org.sfm.jdbc.getter.DoubleIndexedResultSetGetter;
import org.sfm.jdbc.getter.DoubleNamedResultSetGetter;
import org.sfm.jdbc.getter.EnumIndexedResultSetGetter;
import org.sfm.jdbc.getter.EnumNamedResultSetGetter;
import org.sfm.jdbc.getter.FloatIndexedResultSetGetter;
import org.sfm.jdbc.getter.FloatNamedResultSetGetter;
import org.sfm.jdbc.getter.IntIndexedResultSetGetter;
import org.sfm.jdbc.getter.IntNamedResultSetGetter;
import org.sfm.jdbc.getter.LongIndexedResultSetGetter;
import org.sfm.jdbc.getter.LongNamedResultSetGetter;
import org.sfm.jdbc.getter.OrdinalEnumIndexedResultSetGetter;
import org.sfm.jdbc.getter.OrdinalEnumNamedResultSetGetter;
import org.sfm.jdbc.getter.ShortIndexedResultSetGetter;
import org.sfm.jdbc.getter.ShortNamedResultSetGetter;
import org.sfm.jdbc.getter.StringEnumIndexedResultSetGetter;
import org.sfm.jdbc.getter.StringEnumNamedResultSetGetter;
import org.sfm.jdbc.getter.StringIndexedResultSetGetter;
import org.sfm.jdbc.getter.StringNamedResultSetGetter;
import org.sfm.jdbc.getter.TimestampIndexedResultSetGetter;
import org.sfm.jdbc.getter.TimestampNamedResultSetGetter;
import org.sfm.reflect.Getter;

public final class ResultSetGetterFactory {
	public static final int UNDEFINED = -99999;

	public static Getter<ResultSet, ? extends Object> newGetter(
			final Class<? extends Object> type, final String column, final int sqlType) {
		Getter<ResultSet, ? extends Object> getter = null;
		
		if (String.class.isAssignableFrom(type)) {
			getter = new StringNamedResultSetGetter(column);
		} else if (Date.class.isAssignableFrom(type)) {
			getter = new TimestampNamedResultSetGetter(column);
		} else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
			getter = new BooleanNamedResultSetGetter(column);
		} else if (type.equals(Integer.class) || type.equals(int.class)) {
			getter = new IntNamedResultSetGetter(column);
		} else if (type.equals(Long.class) || type.equals(long.class)) {
			getter = new LongNamedResultSetGetter(column);
		} else if (type.equals(Float.class) || type.equals(float.class)) {
			getter = new FloatNamedResultSetGetter(column);
		} else if (type.equals(Double.class) || type.equals(double.class)) {
			getter = new DoubleNamedResultSetGetter(column);
		} else if (type.equals(Byte.class)|| type.equals(byte.class)) {
			getter = new ByteNamedResultSetGetter(column);
		} else if (type.equals(Character.class) || type.equals(char.class)) {
			getter = new CharacterNamedResultSetGetter(column);
		} else if (type.equals(Short.class) || type.equals(short.class)) {
			getter = new ShortNamedResultSetGetter(column);
		} else if (Enum.class.isAssignableFrom(type)) {
			getter = nameEnumGetter(type, column, sqlType, getter);
		}
		
		return getter;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Getter<ResultSet, ? extends Object> nameEnumGetter(
			final Class<? extends Object> type, final String column,
			final int sqlType, Getter<ResultSet, ? extends Object> getter) {
		switch(sqlType) {
		case UNDEFINED: 
			getter = new EnumNamedResultSetGetter(column, type);
			break;
		case Types.BIGINT:
		case Types.INTEGER:
		case Types.NUMERIC:
		case Types.SMALLINT:
		case Types.TINYINT:
			getter = new OrdinalEnumNamedResultSetGetter(column, type);
			break;
		case Types.CHAR:
		case Types.LONGNVARCHAR:
		case Types.LONGVARCHAR:
		case Types.NCHAR:
		case Types.NVARCHAR:
		case Types.VARCHAR:
			getter = new StringEnumNamedResultSetGetter(column, type);
			break;
		}
		return getter;
	}
	
	public static Getter<ResultSet, ? extends Object> newGetter(
			Class<? extends Object> type, int column, int sqlType) {
		Getter<ResultSet, ? extends Object> getter = null;
		if (String.class.isAssignableFrom(type)) {
			getter = new StringIndexedResultSetGetter(column);
		} else if (Date.class.isAssignableFrom(type)) {
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
		} else if (Enum.class.isAssignableFrom(type)) {
			getter = indexEnumGetter(type, column, sqlType, getter);
		}

		return getter;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Getter<ResultSet, ? extends Object> indexEnumGetter(
			Class<? extends Object> type, int column, int sqlType,
			Getter<ResultSet, ? extends Object> getter) {
		switch(sqlType) {
		case UNDEFINED: 
			getter = new EnumIndexedResultSetGetter(column, type);
			break;
		case Types.BIGINT:
		case Types.INTEGER:
		case Types.NUMERIC:
		case Types.SMALLINT:
		case Types.TINYINT:
			getter = new OrdinalEnumIndexedResultSetGetter(column, type);
			break;
		case Types.CHAR:
		case Types.LONGNVARCHAR:
		case Types.LONGVARCHAR:
		case Types.NCHAR:
		case Types.NVARCHAR:
		case Types.VARCHAR:
			getter = new StringEnumIndexedResultSetGetter(column, type);
			break;
		}
		return getter;
	}
}
