package org.sfm.jdbc.impl.setter;

import org.sfm.jdbc.impl.setter.CharacterPreparedStatementIndexedSetter;
import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.CharacterSetter;

import java.sql.PreparedStatement;

public class CharacterPreparedStatementSetter implements Setter<PreparedStatement, Character>, CharacterSetter<PreparedStatement> {

    private final int columnIndex;
    private final CharacterPreparedStatementIndexedSetter setter = new CharacterPreparedStatementIndexedSetter();

    public CharacterPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }


    @Override
    public void setCharacter(PreparedStatement target, char value) throws Exception {
        setter.setCharacter(target, value, columnIndex);
    }

    @Override
    public void set(PreparedStatement target, Character value) throws Exception {
        setter.set(target, value, columnIndex);
    }
}
