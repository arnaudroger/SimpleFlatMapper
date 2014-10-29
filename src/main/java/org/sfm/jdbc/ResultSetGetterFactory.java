package org.sfm.jdbc;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Date;

import org.sfm.jdbc.getter.BooleanIndexedResultSetGetter;
import org.sfm.jdbc.getter.ByteIndexedResultSetGetter;
import org.sfm.jdbc.getter.CharacterIndexedResultSetGetter;
import org.sfm.jdbc.getter.DoubleIndexedResultSetGetter;
import org.sfm.jdbc.getter.EnumIndexedResultSetGetter;
import org.sfm.jdbc.getter.FloatIndexedResultSetGetter;
import org.sfm.jdbc.getter.IntIndexedResultSetGetter;
import org.sfm.jdbc.getter.LongIndexedResultSetGetter;
import org.sfm.jdbc.getter.OrdinalEnumIndexedResultSetGetter;
import org.sfm.jdbc.getter.ShortIndexedResultSetGetter;
import org.sfm.jdbc.getter.StringEnumIndexedResultSetGetter;
import org.sfm.jdbc.getter.StringIndexedResultSetGetter;
import org.sfm.jdbc.getter.TimestampIndexedResultSetGetter;
import org.sfm.map.GetterFactory;
import org.sfm.map.MapperBuildingException;
import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;

public final class ResultSetGetterFactory implements GetterFactory<ResultSet, JdbcColumnKey>{
	public static final int UNDEFINED = -99999;

	@SuppressWarnings("unchecked")
	private <P> Getter<ResultSet, P> newIndexedGetter(
			Type genericType, JdbcColumnKey key) {
		Getter<ResultSet, P> getter = null;
		
		Class<?> type = TypeHelper.toClass(genericType);
		int column = key.getIndex();
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

	private <P extends Enum<P>> Getter<ResultSet, P> indexEnumGetter(Class<P> type, JdbcColumnKey key) {
		int column = key.getIndex();
		switch(key.getSqlType()) {
		case JdbcColumnKey.UNDEFINED_TYPE: 
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
	
	@Override
	public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key) {
		return newIndexedGetter(genericType, key);
	}
}
