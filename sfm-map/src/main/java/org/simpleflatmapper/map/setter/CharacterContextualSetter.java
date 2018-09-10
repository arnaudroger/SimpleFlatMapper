package org.simpleflatmapper.map.setter;

import org.simpleflatmapper.converter.Context;

public interface CharacterContextualSetter<T> {
	void setCharacter(T target, char value, Context context) throws Exception;
}
