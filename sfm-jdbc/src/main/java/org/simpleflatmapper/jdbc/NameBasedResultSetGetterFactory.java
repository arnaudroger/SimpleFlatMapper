package org.simpleflatmapper.jdbc;


import org.simpleflatmapper.map.mapper.AbstractColumnNameDiscriminatorMapperFactory;
import org.simpleflatmapper.reflect.Getter;

import java.sql.ResultSet;

public final class NameBasedResultSetGetterFactory implements AbstractColumnNameDiscriminatorMapperFactory.ColumnNameGetterFactory<ResultSet> {
	public static final NameBasedResultSetGetterFactory INSTANCE = new NameBasedResultSetGetterFactory();

	private NameBasedResultSetGetterFactory() {
	}

	

	@SuppressWarnings("unchecked")
	@Override
	public <T> Getter<? super ResultSet, ? extends T> getGetter(String discriminatorColumn, Class<T> discriminatorType) {
		if (char.class.equals(discriminatorType) || Character.class.equals(discriminatorType)) {
			return (Getter<? super ResultSet, ? extends T>) new Getter<ResultSet, Character>() {
				@Override
				public Character get(ResultSet target) throws Exception {
					char i = (char) target.getInt(discriminatorColumn);
					if (target.wasNull()) {
						return null;
					}
					return i;
				}
			};
		}
		if (byte.class.equals(discriminatorType) || Byte.class.equals(discriminatorType)) {
			return (Getter<? super ResultSet, ? extends T>) new Getter<ResultSet, Byte>() {
				@Override
				public Byte get(ResultSet target) throws Exception {
					byte i = target.getByte(discriminatorColumn);
					if (target.wasNull()) {
						return null;
					}
					return i;
				}
			};
		}
		if (short.class.equals(discriminatorType) || Short.class.equals(discriminatorType)) {
			return (Getter<? super ResultSet, ? extends T>) new Getter<ResultSet, Short>() {
				@Override
				public Short get(ResultSet target) throws Exception {
					short i = target.getShort(discriminatorColumn);
					if (target.wasNull()) {
						return null;
					}
					return i;
				}
			};
		}
		if (long.class.equals(discriminatorType) || Long.class.equals(discriminatorType)) {
			return (Getter<? super ResultSet, ? extends T>) new Getter<ResultSet, Long>() {
				@Override
				public Long get(ResultSet target) throws Exception {
					long i = target.getLong(discriminatorColumn);
					if (target.wasNull()) {
						return null;
					}
					return i;
				}
			};
		}
		if (int.class.equals(discriminatorType) || Integer.class.equals(discriminatorType)) {
			return (Getter<? super ResultSet, ? extends T>) new Getter<ResultSet, Integer>() {
				@Override
				public Integer get(ResultSet target) throws Exception {
					int i = target.getInt(discriminatorColumn);
					if (target.wasNull()) {
						return null;
					}
					return i;
				}
			};
		}
		if (float.class.equals(discriminatorType) || Float.class.equals(discriminatorType)) {
			return (Getter<? super ResultSet, ? extends T>) new Getter<ResultSet, Float>() {
				@Override
				public Float get(ResultSet target) throws Exception {
					float i = target.getFloat(discriminatorColumn);
					if (target.wasNull()) {
						return null;
					}
					return i;
				}
			};
		}
		if (double.class.equals(discriminatorType) || Double.class.equals(discriminatorType)) {
			return (Getter<? super ResultSet, ? extends T>) new Getter<ResultSet, Double>() {
				@Override
				public Double get(ResultSet target) throws Exception {
					double i = target.getDouble(discriminatorColumn);
					if (target.wasNull()) {
						return null;
					}
					return i;
				}
			};
		}
		if (String.class.equals(discriminatorType)) {
			return (Getter<? super ResultSet, ? extends T>) new Getter<ResultSet, String>() {
				@Override
				public String get(ResultSet target) throws Exception {
					return target.getString(discriminatorColumn);
				}
			};
		}
		if (Object.class.equals(discriminatorType)) {
			return (Getter<? super ResultSet, ? extends T>) new Getter<ResultSet, Object>() {
				@Override
				public Object get(ResultSet target) throws Exception {
					return target.getObject(discriminatorColumn);
				}
			};
		}
		
		return null;
	}
}
