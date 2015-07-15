package org.sfm.jdbc;

import org.sfm.map.FieldKey;
import org.sfm.map.impl.MapperKey;
import org.sfm.map.impl.TypeAffinity;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

import static org.sfm.utils.Asserts.requireNonNull;

public class JdbcColumnKey implements FieldKey<JdbcColumnKey>, TypeAffinity {

	public static final int UNDEFINED_TYPE = -99999;

	private final String name;
	private final int index;
	private final int sqlType;
	private final JdbcColumnKey parent;

	public JdbcColumnKey(String columnName, int columnIndex) {
		this.name = requireNonNull("columnName", columnName);
		this.index = columnIndex;
		this.sqlType = UNDEFINED_TYPE;
		this.parent = null;
	}

	public JdbcColumnKey(String columnName, int columnIndex, int sqlType) {
		this.name = requireNonNull("columnName", columnName);
		this.index = columnIndex;
		this.sqlType = sqlType;
		this.parent = null;
	}

	public JdbcColumnKey(String columnName, int columnIndex, int sqlType, JdbcColumnKey parent) {
		this.name = requireNonNull("columnName", columnName);
		this.index = columnIndex;
		this.sqlType = sqlType;
		this.parent = parent;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getIndex() {
		return index;
	}

	public int getSqlType() {
		return sqlType;
	}

	public JdbcColumnKey getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return "ColumnKey [columnName=" + name + ", columnIndex="
				+ index + ", sqlType=" + sqlType + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + (name.hashCode());
		result = prime * result + sqlType;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JdbcColumnKey other = (JdbcColumnKey) obj;
		if (index != other.index)
			return false;
        if (!name.equals(other.name))
            return false;
		if (sqlType != other.sqlType)
			return false;
		return true;
	}

	@Override
	public JdbcColumnKey alias(String alias) {
		return new JdbcColumnKey(alias, index, sqlType, this);
	}

	public static JdbcColumnKey of(ResultSetMetaData metaData, int columnIndex) throws SQLException {
		return new JdbcColumnKey(metaData.getColumnLabel(columnIndex), columnIndex, metaData.getColumnType(columnIndex));
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
}
