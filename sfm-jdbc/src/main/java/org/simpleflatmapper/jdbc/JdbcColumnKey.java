package org.simpleflatmapper.jdbc;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.mapper.MapperKey;
import org.simpleflatmapper.reflect.TypeAffinity;
import org.simpleflatmapper.util.TypeHelper;

import java.io.ObjectInputStream;
import java.lang.reflect.Type;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

public final class JdbcColumnKey extends FieldKey<JdbcColumnKey> implements TypeAffinity {

	public static final int UNDEFINED_TYPE = -99999;

	private final int sqlType;
	private final String columnClass;

	public JdbcColumnKey(String name, int index) {
		super(name, index);
		this.sqlType = UNDEFINED_TYPE;
		this.columnClass = null;
	}

	public JdbcColumnKey(String name, int index, int sqlType) {
		super(name, index);
		this.sqlType = sqlType;
		this.columnClass = null;
	}

	public JdbcColumnKey(String name, int index, int sqlType, JdbcColumnKey parent) {
		super(name, index, parent);
		this.sqlType = sqlType;
		this.columnClass = null;
	}

	public JdbcColumnKey(String name, int index, int sqlType, String columnClass) {
		super(name, index);
		this.sqlType = sqlType;
		this.columnClass = columnClass;
	}

	public JdbcColumnKey(String name, int index, int sqlType, String columnClass, JdbcColumnKey parent) {
		super(name, index, parent);
		this.sqlType = sqlType;
		this.columnClass = columnClass;
	}

	public int getSqlType(Object[] properties) { 
		if (properties != null) {
			for(int i = 0; i < properties.length; i++) {
				Object prop = properties[i];
				if (prop instanceof SqlTypeColumnProperty) {
					return ((SqlTypeColumnProperty)prop).getSqlType();
				}
			}
		}
		return sqlType;
	}

	@Override
	public String toString() {
		return "ColumnKey [columnName=" + name + ", columnIndex="
				+ index + ", sqlType=" + sqlType + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		JdbcColumnKey that = (JdbcColumnKey) o;

		return sqlType == that.sqlType;

	}

	public String getColumnClass() {
		return columnClass;
	}

	@Override
	public Type getType(Type targetType) {
		if (columnClass != null && columnClass.length() > 0) {
			try {
				Class<?> jdbcDriverClass = Class.forName(columnClass);

				// only consider column class if compatible with object
				// is a primitive or a java. class
				// to avoid oracle.sql.TIMESTAMPTZ malarchy
				// any other class won't have a converter
				// and should go through the regular sql type
				if (TypeHelper.isAssignable(targetType, jdbcDriverClass)
						|| jdbcDriverClass.isPrimitive()
						|| columnClass.startsWith("java.")) {
					return jdbcDriverClass;
				}
			} catch (ClassNotFoundException e) {
				// ignore not much can be done
			}
		}
		return JdbcTypeHelper.toJavaType(sqlType, targetType);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + sqlType;
		return result;
	}

	@Override
	public JdbcColumnKey alias(String alias) {
		return new JdbcColumnKey(alias, index, sqlType, columnClass,this);
	}

	public static JdbcColumnKey of(ResultSetMetaData metaData, int columnIndex) throws SQLException {
		return new JdbcColumnKey(metaData.getColumnLabel(columnIndex), columnIndex, metaData.getColumnType(columnIndex), metaData.getColumnClassName(columnIndex));
	}

	public static MapperKey<JdbcColumnKey> mapperKey(ResultSetMetaData metaData)  throws  SQLException {
		JdbcColumnKey[] keys = new JdbcColumnKey[metaData.getColumnCount()];

		for(int i = 1; i <= metaData.getColumnCount(); i++) {
			keys[i - 1] = of(metaData, i);
		}

		return new MapperKey<JdbcColumnKey>(keys);
	}

	@Override
	public Class<?>[] getAffinities() {
		switch (sqlType) {
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
			case Types.NVARCHAR:
			case Types.LONGNVARCHAR:
				return new Class[] { String.class };
			case Types.BIGINT:
				return new Class[] { Long.class, Integer.class, Number.class};
			case Types.SMALLINT:
			case Types.INTEGER:
				return new Class[] { Integer.class, Long.class, Number.class};
			case Types.DECIMAL:
			case Types.DOUBLE:
				return new Class[] { Double.class, Float.class, Number.class};
			case Types.FLOAT:
				return new Class[] { Float.class, Double.class, Number.class};
			case Types.NUMERIC:
				return new Class[] { Double.class, Long.class, Number.class};
			case Types.BOOLEAN:
				return new Class[] { Boolean.class, Integer.class, Long.class, Number.class};
			case Types.TIMESTAMP:
			case Types.DATE:
			case Types.TIME:
				return new Class[] { Date.class, Long.class};
		}
		return null;
	}

	public String getOrginalName() {
		return parent != null ? parent.getOrginalName() : getName();
	}
}
