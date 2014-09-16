package org.sfm.jdbc;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Date;

import org.sfm.builder.GetterFactory;
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
import org.sfm.map.MapperBuildingException;
import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;

public final class ResultSetGetterFactory implements GetterFactory<ResultSet, ColumnKey>{
	public static final int UNDEFINED = -99999;

	@SuppressWarnings("unchecked")
	private <P> Getter<ResultSet, P> newNamedGetter(
			final Type genericType, final ColumnKey key) {
		Getter<ResultSet, P> getter = null;
		
		Class<?> type = TypeHelper.toClass(genericType);
		final String column = key.getColumnName();
		
		if (String.class.isAssignableFrom(type)) {
			getter = (Getter<ResultSet, P>) new StringNamedResultSetGetter(column);
		} else if (Date.class.isAssignableFrom(type)) {
			getter = (Getter<ResultSet, P>) new TimestampNamedResultSetGetter(column);
		} else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
			getter = (Getter<ResultSet, P>) new BooleanNamedResultSetGetter(column);
		} else if (type.equals(Integer.class) || type.equals(int.class)) {
			getter = (Getter<ResultSet, P>) new IntNamedResultSetGetter(column);
		} else if (type.equals(Long.class) || type.equals(long.class)) {
			getter = (Getter<ResultSet, P>) new LongNamedResultSetGetter(column);
		} else if (type.equals(Float.class) || type.equals(float.class)) {
			getter = (Getter<ResultSet, P>) new FloatNamedResultSetGetter(column);
		} else if (type.equals(Double.class) || type.equals(double.class)) {
			getter = (Getter<ResultSet, P>) new DoubleNamedResultSetGetter(column);
		} else if (type.equals(Byte.class)|| type.equals(byte.class)) {
			getter = (Getter<ResultSet, P>) new ByteNamedResultSetGetter(column);
		} else if (type.equals(Character.class) || type.equals(char.class)) {
			getter = (Getter<ResultSet, P>) new CharacterNamedResultSetGetter(column);
		} else if (type.equals(Short.class) || type.equals(short.class)) {
			getter = (Getter<ResultSet, P>) new ShortNamedResultSetGetter(column);
		} else if (Enum.class.isAssignableFrom(type)) {
			@SuppressWarnings("rawtypes")
			Class<? extends Enum> enumClass = (Class<? extends Enum>) type; 
			getter = nameEnumGetter(enumClass, key);
		}
		
		return getter;
	}


	
	@SuppressWarnings("unchecked")
	private <P> Getter<ResultSet, P> newIndexedGetter(
			Type genericType, ColumnKey key) {
		Getter<ResultSet, P> getter = null;
		
		Class<?> type = TypeHelper.toClass(genericType);
		int column = key.getColumnIndex();
		if (String.class.isAssignableFrom(type)) {
			getter = (Getter<ResultSet, P>) new StringIndexedResultSetGetter(column);
		} else if (Date.class.isAssignableFrom(type)) {
			getter = (Getter<ResultSet, P>) new TimestampIndexedResultSetGetter(column);
		} else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
			getter = (Getter<ResultSet, P>) new BooleanIndexedResultSetGetter(column);
		} else if (type.equals(Integer.class) || type.equals(int.class)) {
			getter = (Getter<ResultSet, P>) new IntIndexedResultSetGetter(column);
		} else if (type.equals(Long.class) || type.equals(long.class)) {
			getter = (Getter<ResultSet, P>) new LongIndexedResultSetGetter(column);
		} else if (type.equals(Float.class) || type.equals(float.class)) {
			getter = (Getter<ResultSet, P>) new FloatIndexedResultSetGetter(column);
		} else if (type.equals(Double.class) || type.equals(double.class)) {
			getter = (Getter<ResultSet, P>) new DoubleIndexedResultSetGetter(column);
		} else if (type.equals(Byte.class) || type.equals(byte.class)) {
			getter = (Getter<ResultSet, P>) new ByteIndexedResultSetGetter(column);
		} else if (type.equals(Character.class) || type.equals(char.class)) {
			getter = (Getter<ResultSet, P>) new CharacterIndexedResultSetGetter(column);
		} else if (type.equals(Short.class) || type.equals(short.class)) {
			getter = (Getter<ResultSet, P>) new ShortIndexedResultSetGetter(column);
		} else if (Enum.class.isAssignableFrom(type)) {
			@SuppressWarnings("rawtypes")
			Class<? extends Enum> enumClass = (Class<? extends Enum>) type; 
			getter = indexEnumGetter(enumClass, key);
		}

		return getter;
	}

	private <P extends Enum<P>> Getter<ResultSet, P> indexEnumGetter(Class<P> type, ColumnKey key) {
		int column = key.getColumnIndex();
		switch(key.getSqlType()) {
		case ColumnKey.UNDEFINED_TYPE: 
			return new EnumIndexedResultSetGetter<P>(column, type);
		case Types.BIGINT:
		case Types.INTEGER:
		case Types.NUMERIC:
		case Types.SMALLINT:
		case Types.TINYINT:
			return  new OrdinalEnumIndexedResultSetGetter<P>(column, type);
		case Types.CHAR:
		case Types.LONGNVARCHAR:
		case Types.LONGVARCHAR:
		case Types.NCHAR:
		case Types.NVARCHAR:
		case Types.VARCHAR:
			return  new StringEnumIndexedResultSetGetter<P>(column, type);
		default:
			throw new MapperBuildingException("Incompatible type " + key.getSqlType() + " with enum");
		}
	}
	private <P extends Enum<P>> Getter<ResultSet, P> nameEnumGetter(
			final Class<P> type, final ColumnKey key) {
		String column = key.getColumnName();
		switch(key.getSqlType()) {
		case ColumnKey.UNDEFINED_TYPE: 
			return new EnumNamedResultSetGetter<P>(column, type);
		case Types.BIGINT:
		case Types.INTEGER:
		case Types.NUMERIC:
		case Types.SMALLINT:
		case Types.TINYINT:
			return new OrdinalEnumNamedResultSetGetter<P>(column, type);
		case Types.CHAR:
		case Types.LONGNVARCHAR:
		case Types.LONGVARCHAR:
		case Types.NCHAR:
		case Types.NVARCHAR:
		case Types.VARCHAR:
			return new StringEnumNamedResultSetGetter<P>(column, type);
		default:
			throw new MapperBuildingException("Incompatible type " + key.getSqlType() + " with enum");
		}
	}
	@Override
	public <P> Getter<ResultSet, P> newGetter(Type genericType, ColumnKey key) {
		if (key.hasColumnIndex()) {
			return newIndexedGetter(genericType, key);
		} else {
			return newNamedGetter(genericType, key);
		}
	}
}
