package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.reflect.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;

public final class SQLXMLResultSetGetter implements Getter<ResultSet, SQLXML>, ContextualGetter<ResultSet, SQLXML> {
	private final int column;
	
	public SQLXMLResultSetGetter(final int column) {
		this.column = column;
	}

	public SQLXML get(final ResultSet target) throws SQLException {
		return target.getSQLXML(column);
	}

	@Override
	public SQLXML get(ResultSet resultSet, Context context) throws Exception {
		return get(resultSet);
	}

	@Override
    public String toString() {
        return "SQLXMLResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
