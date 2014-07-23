package org.sfm.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.CharacterGetter;

public class CharacterIndexedResultSetGetter implements CharacterGetter<ResultSet>, Getter<ResultSet, Character> {

	private final int column;
	
	public CharacterIndexedResultSetGetter(int column) {
		this.column = column;
	}

	@Override
	public char getCharacter(ResultSet target) throws SQLException {
		return (char)target.getInt(column);
	}
	
	@Override
	public Character get(ResultSet target) throws SQLException {
		return getCharacter(target);
	}
}
