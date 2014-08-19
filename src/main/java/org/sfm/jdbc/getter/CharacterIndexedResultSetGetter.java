package org.sfm.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.CharacterGetter;

public final class CharacterIndexedResultSetGetter implements CharacterGetter<ResultSet>, Getter<ResultSet, Character> {

	private final int column;
	
	public CharacterIndexedResultSetGetter(final int column) {
		this.column = column;
	}

	@Override
	public char getCharacter(final ResultSet target) throws SQLException {
		return (char)target.getInt(column);
	}
	
	@Override
	public Character get(final ResultSet target) throws SQLException {
		final char c = getCharacter(target);
		if (target.wasNull()) {
			return null;
		} else {
			return Character.valueOf(c);
		}
	}
}
