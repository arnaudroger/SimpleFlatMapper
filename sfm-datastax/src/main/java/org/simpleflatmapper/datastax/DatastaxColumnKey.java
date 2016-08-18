package org.simpleflatmapper.datastax;

import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.ColumnMetadata;
import com.datastax.driver.core.DataType;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.mapper.MapperKey;
import org.simpleflatmapper.reflect.TypeAffinity;

import java.lang.reflect.Type;

public final class DatastaxColumnKey extends FieldKey<DatastaxColumnKey> implements TypeAffinity {

	private final DataType dataType;
	private final DatastaxColumnKey parent;

	public DatastaxColumnKey(String name, int index) {
		super(name, index);
		this.dataType = null;
		this.parent = null;
	}

	public DatastaxColumnKey(String name, int index, DataType dataType) {
		super(name, index);
		this.dataType = dataType;
		this.parent = null;
	}

	public DatastaxColumnKey(String name, int index, DataType dataType, DatastaxColumnKey parent) {
		super(name, index);
		this.dataType = dataType;
		this.parent = parent;
	}

	public DataType getDataType() {
		return dataType;
	}


	@Override
	public String toString() {
		return "DatastaxColumnKey{" +
				"name='" + name + '\'' +
				", index=" + index +
				", dataType=" + dataType +
				", parent=" + parent +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		DatastaxColumnKey that = (DatastaxColumnKey) o;

		return dataType != null ? dataType.equals(that.dataType) : that.dataType == null;

	}

	@Override
	public Type getType(Type targetType) {
		return DataTypeHelper.asJavaClass(dataType, targetType);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (dataType != null ? dataType.hashCode() : 0);
		return result;
	}

	@Override
	public DatastaxColumnKey alias(String alias) {
		return new DatastaxColumnKey(alias, index, dataType, this);
	}

	public static DatastaxColumnKey of(ColumnDefinitions metaData, int column) {
		return new DatastaxColumnKey(metaData.getName(column), column , metaData.getType(column));
	}

	public static DatastaxColumnKey of(ColumnMetadata metaData, int column) {
		return new DatastaxColumnKey(metaData.getName(), column , metaData.getType());
	}

	public static MapperKey<DatastaxColumnKey> mapperKey(ColumnDefinitions metaData) {
		DatastaxColumnKey[] keys = new DatastaxColumnKey[metaData.size()];

		for(int i = 0; i < metaData.size(); i++) {
			keys[i] = of(metaData, i);
		}

		return new MapperKey<DatastaxColumnKey>(keys);
	}

	@Override
	public Class<?>[] getAffinities()     {
		if (dataType != null) {
			final Class<?> aClass = DataTypeHelper.asJavaClass(dataType);
			if (Number.class.isAssignableFrom(aClass)) {
				return new Class<?>[] { aClass, Number.class };
			}
			return new Class<?>[] {aClass};
		}
		return null;
	}

	public DatastaxColumnKey datatype(DataType datatype) {
		return new DatastaxColumnKey(this.getName(), this.index, datatype, parent);
	}
}
