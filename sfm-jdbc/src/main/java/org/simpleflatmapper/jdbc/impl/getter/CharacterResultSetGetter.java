package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.CharacterContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.CharacterGetter;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class CharacterResultSetGetter implements
		CharacterGetter<ResultSet>, Getter<ResultSet, Character>,
		CharacterContextualGetter<ResultSet>, ContextualGetter<ResultSet, Character>
{

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
	public char getCharacter(ResultSet resultSet, Context mappingContext) throws Exception {
		return getCharacter(resultSet);
	}

	@Override
	public Character get(ResultSet resultSet, Context context) throws Exception {
		return get(resultSet);
	}

    @Override
    public String toString() {
        return "CharacterResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
