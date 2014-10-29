package org.sfm.jdbc;

public class ColumnKey {
	public static final int UNDEFINED_TYPE = -99999;
	private final String columnName;
	private final int columnIndex;
	private final int sqlType;

	public ColumnKey(String columnName) {
		this.columnName = columnName;
		this.columnIndex = -1;
		this.sqlType = UNDEFINED_TYPE;
	}
	public ColumnKey(String columnName, int columnIndex) {
		this.columnName = columnName;
		this.columnIndex = columnIndex;
		this.sqlType = UNDEFINED_TYPE;
	}

	public ColumnKey(String columnName, int columnIndex, int sqlType) {
		this.columnName = columnName;
		this.columnIndex = columnIndex;
		this.sqlType = sqlType;
	}

	public String getColumnName() {
		return columnName;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public int getSqlType() {
		return sqlType;
	}

	@Override
	public String toString() {
		return "ColumnKey [columnName=" + columnName + ", columnIndex="
				+ columnIndex + ", sqlType=" + sqlType + "]";
	}

	public boolean hasColumnIndex() {
		return columnIndex != -1;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + columnIndex;
		result = prime * result
				+ ((columnName == null) ? 0 : columnName.hashCode());
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
		ColumnKey other = (ColumnKey) obj;
		if (columnIndex != other.columnIndex)
			return false;
		if (columnName == null) {
			if (other.columnName != null)
				return false;
		} else if (!columnName.equals(other.columnName))
			return false;
		if (sqlType != other.sqlType)
			return false;
		return true;
	}
}
