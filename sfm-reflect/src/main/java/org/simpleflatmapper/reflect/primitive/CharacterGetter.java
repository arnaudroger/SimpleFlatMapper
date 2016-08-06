package org.simpleflatmapper.reflect.primitive;


public interface CharacterGetter<T> {
	char getCharacter(T target)  throws Exception;
}
