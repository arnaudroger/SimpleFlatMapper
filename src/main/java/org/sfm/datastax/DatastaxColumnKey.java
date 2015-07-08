package org.sfm.datastax;

import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.DataType;
import org.sfm.map.FieldKey;
import org.sfm.map.impl.MapperKey;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static org.sfm.utils.Asserts.requireNonNull;

public class DatastaxColumnKey implements FieldKey<DatastaxColumnKey> {

	private final String name;
	private final int index;
	private final DataType sqlType;
	private final DatastaxColumnKey parent;

	public DatastaxColumnKey(String columnName, int columnIndex) {
		this.name = requireNonNull("columnName", columnName);
		this.index = columnIndex;
		this.sqlType = null;
		this.parent = null;
	}

	public DatastaxColumnKey(String columnName, int columnIndex, DataType sqlType) {
		this.name = requireNonNull("columnName", columnName);
		this.index = columnIndex;
		this.sqlType = sqlType;
		this.parent = null;
	}

	public DatastaxColumnKey(String columnName, int columnIndex, DataType sqlType, DatastaxColumnKey parent) {
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

	public DataType getDateType() {
		return sqlType;
	}

	public DatastaxColumnKey getParent() {
		return parent;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DatastaxColumnKey that = (DatastaxColumnKey) o;

		if (index != that.index) return false;
		if (!name.equals(that.name)) return false;
		return !(sqlType != null ? !sqlType.equals(that.sqlType) : that.sqlType != null);

	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + index;
		result = 31 * result + (sqlType != null ? sqlType.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "DatastaxColumnKey{" +
				"name='" + name + '\'' +
				", index=" + index +
				", sqlType=" + sqlType +
				", parent=" + parent +
				'}';
	}

	@Override
	public DatastaxColumnKey alias(String alias) {
		return new DatastaxColumnKey(alias, index, sqlType, this);
	}

	public static DatastaxColumnKey of(ColumnDefinitions metaData, int column) throws SQLException {
		return new DatastaxColumnKey(metaData.getName(column), column , metaData.getType(column));
	}

	public static MapperKey<DatastaxColumnKey> mapperKey(ColumnDefinitions metaData)  throws  SQLException {
		DatastaxColumnKey[] keys = new DatastaxColumnKey[metaData.size()];

		for(int i = 1; i <= metaData.size(); i++) {
			keys[i - 1] = of(metaData, i);
		}

		return new MapperKey<DatastaxColumnKey>(keys);
	}
}
