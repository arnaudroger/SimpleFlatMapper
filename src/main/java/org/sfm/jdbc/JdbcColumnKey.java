package org.sfm.jdbc;

import org.sfm.map.impl.FieldKey;

public class JdbcColumnKey implements FieldKey<JdbcColumnKey> {

	public static final int UNDEFINED_TYPE = -99999;

	private final String name;
	private final int index;
	private final int sqlType;
	private final JdbcColumnKey parent;

	public JdbcColumnKey(String columnName, int columnIndex) {
		this.name = columnName;
		this.index = columnIndex;
		this.sqlType = UNDEFINED_TYPE;
		this.parent = null;
	}

	public JdbcColumnKey(String columnName, int columnIndex, int sqlType) {
		this.name = columnName;
		this.index = columnIndex;
		this.sqlType = sqlType;
		this.parent = null;
	}

	public JdbcColumnKey(String columnName, int columnIndex, int sqlType, JdbcColumnKey parent) {
		this.name = columnName;
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
		result = prime * result
				+ ((name == null) ? 0 : name.hashCode());
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (sqlType != other.sqlType)
			return false;
		return true;
	}

	@Override
	public JdbcColumnKey alias(String alias) {
		return new JdbcColumnKey(alias, index, sqlType, this);
	}
}
