package org.simpleflatmapper.mockdb;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

public class MockResultSetMetaData implements ResultSetMetaData {

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getColumnCount() throws SQLException {
		return 4;
	}

	@Override
	public boolean isAutoIncrement(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCaseSensitive(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSearchable(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCurrency(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int isNullable(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSigned(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getColumnDisplaySize(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getColumnLabel(int column) throws SQLException {
		return getColumnName(column);
	}

	@Override
	public String getColumnName(int column) throws SQLException {
		switch (column) {
		case 1: return "id";
		case 2: return "name";
		case 3: return "email";
		case 4: return "year_started";
		default:
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public String getSchemaName(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getPrecision(int column) throws SQLException {
		return 0;
	}

	@Override
	public int getScale(int column) throws SQLException {
		return 0;
	}

	@Override
	public String getTableName(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCatalogName(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getColumnType(int column) throws SQLException {
		switch (column) {
		case 1: return Types.BIGINT;
		case 2: return Types.VARCHAR;
		case 3: return Types.VARCHAR;
		case 4: return Types.INTEGER;
		default:
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public String getColumnTypeName(int column) throws SQLException {
		switch (column) {
		case 1: return "bigint";
		case 2: return "varchar";
		case 3: return  "varchar";
		case 4: return "int";
		default:
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public boolean isReadOnly(int column) throws SQLException {
		return true;
	}

	@Override
	public boolean isWritable(int column) throws SQLException {
		return false;
	}

	@Override
	public boolean isDefinitelyWritable(int column) throws SQLException {
		return false;
	}

	@Override
	public String getColumnClassName(int column) throws SQLException {
		return "";
	}

}
