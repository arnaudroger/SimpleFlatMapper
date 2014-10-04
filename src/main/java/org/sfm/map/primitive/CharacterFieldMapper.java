package org.sfm.map.primitive;

import org.sfm.map.FieldMapper;
import org.sfm.reflect.primitive.CharacterGetter;
import org.sfm.reflect.primitive.CharacterSetter;

public final class CharacterFieldMapper<S, T> implements FieldMapper<S, T> {

	private final CharacterGetter<S> getter;
	private final CharacterSetter<T> setter;
	
 	public CharacterFieldMapper(final CharacterGetter<S> getter, final CharacterSetter<T> setter) {
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	public void map(final S source, final T target) throws Exception {
		setter.setCharacter(target, getter.getCharacter(source));
	}
}
