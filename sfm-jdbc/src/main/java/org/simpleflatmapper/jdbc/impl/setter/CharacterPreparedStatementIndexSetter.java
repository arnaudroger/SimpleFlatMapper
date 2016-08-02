package org.simpleflatmapper.jdbc.impl.setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class CharacterPreparedStatementIndexSetter implements PreparedStatementIndexSetter<Character> {

    @Override
    public void set(PreparedStatement target, Character value, int columnIndex) throws SQLException {
        if (value != null) {
            target.setInt(columnIndex, value);
        } else {
            target.setNull(columnIndex, Types.INTEGER);
        }
    }

    public void setCharacter(PreparedStatement target, char value, int columnIndex) throws Exception {
        target.setInt(columnIndex, value);
    }
}
