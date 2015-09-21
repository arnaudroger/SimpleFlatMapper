package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class URLPreparedStatementSetter implements Setter<PreparedStatement, URL> {
    private final int columnIndex;

    public URLPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void set(PreparedStatement target, URL value) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.DATALINK);
        } else {
            target.setURL(columnIndex, value);
        }
    }
}
