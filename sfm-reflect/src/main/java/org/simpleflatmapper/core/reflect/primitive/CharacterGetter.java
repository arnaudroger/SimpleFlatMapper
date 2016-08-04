package org.simpleflatmapper.core.reflect.primitive;


public interface CharacterGetter<T> {
	char getCharacter(T target)  throws Exception;
}
