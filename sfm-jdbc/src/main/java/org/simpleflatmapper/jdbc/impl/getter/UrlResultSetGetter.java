package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.reflect.Getter;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class UrlResultSetGetter implements
		Getter<ResultSet, URL>, ContextualGetter<ResultSet, URL> {
	private final int column;
	
	public UrlResultSetGetter(final int column) {
		this.column = column;
	}

	public URL get(final ResultSet target) throws SQLException {
		return target.getURL(column);
	}

	@Override
	public URL get(ResultSet resultSet, Context context) throws Exception {
		return get(resultSet);
	}

    @Override
    public String toString() {
        return "UrlResultSetGetter{" +
                "property=" + column +
                '}';
    }


}
