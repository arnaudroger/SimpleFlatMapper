package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.CharacterSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class CharacterPreparedStatementSetter implements Setter<PreparedStatement, Character>, CharacterSetter<PreparedStatement> {
    private final int columnIndex;

    public CharacterPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void set(PreparedStatement target, Character value) throws SQLException {
        if (value != null) {
            target.setInt(columnIndex, value);
        } else {
            target.setNull(columnIndex, Types.INTEGER);
        }
    }

    @Override
    public void setCharacter(PreparedStatement target, char value) throws Exception {
        target.setInt(columnIndex, value);
    }
}
