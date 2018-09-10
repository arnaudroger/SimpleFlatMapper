package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.setter.CharacterContextualSetter;
import org.simpleflatmapper.map.setter.ContextualSetter;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.CharacterSetter;

import java.sql.PreparedStatement;

public class CharacterPreparedStatementSetter implements ContextualSetter<PreparedStatement, Character>, CharacterContextualSetter<PreparedStatement> {

    private final int columnIndex;
    private final CharacterPreparedStatementIndexSetter setter = new CharacterPreparedStatementIndexSetter();

    public CharacterPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }


    @Override
    public void setCharacter(PreparedStatement target, char value, Context context) throws Exception {
        setter.setCharacter(target, value, columnIndex, context);
    }

    @Override
    public void set(PreparedStatement target, Character value, Context context) throws Exception {
        setter.set(target, value, columnIndex, context);
    }
}
