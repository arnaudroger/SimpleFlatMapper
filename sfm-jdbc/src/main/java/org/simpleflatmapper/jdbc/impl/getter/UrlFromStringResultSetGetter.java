package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.reflect.Getter;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class UrlFromStringResultSetGetter implements
		Getter<java.sql.ResultSet, URL>, ContextualGetter<ResultSet, URL> {
	private final int column;
	
	public UrlFromStringResultSetGetter(final int column) {
		this.column = column;
	}

	public URL get(final ResultSet target) throws SQLException {
		try {
			return new URL(target.getString(column));
		} catch (MalformedURLException e) {
			throw new SQLException(e.getMessage(), e);
		}
	}

	@Override
	public URL get(ResultSet resultSet, Context context) throws Exception {
		return get(resultSet);
	}

    @Override
    public String toString() {
        return "UrlFromStringResultSetGetter{" +
                "property=" + column +
                '}';
    }


}
