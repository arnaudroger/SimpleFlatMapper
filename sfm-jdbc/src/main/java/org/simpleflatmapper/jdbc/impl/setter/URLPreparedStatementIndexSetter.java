package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class URLPreparedStatementIndexSetter implements PreparedStatementIndexSetter<URL> {
    @Override
    public void set(PreparedStatement target, URL value, int columnIndex, Context context) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.DATALINK);
        } else {
            target.setURL(columnIndex, value);
        }
    }
}
