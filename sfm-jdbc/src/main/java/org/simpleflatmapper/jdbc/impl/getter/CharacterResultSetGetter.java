package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.CharacterGetter;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class CharacterResultSetGetter implements CharacterGetter<ResultSet>, Getter<ResultSet, Character> {

	private final int column;
	
	public CharacterResultSetGetter(final int column) {
		this.column = column;
	}

	@Override
	public char getCharacter(final ResultSet target) throws SQLException {
		return (char)target.getInt(column);
	}
	
	@Override
	public Character get(final ResultSet target) throws SQLException {
		final char c = getCharacter(target);
		if (c == 0 && target.wasNull()) {
			return null;
		} else {
			return c;
		}
	}

    @Override
    public String toString() {
        return "CharacterResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
