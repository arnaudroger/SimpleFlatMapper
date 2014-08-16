package org.sfm.map.primitive;

import org.sfm.map.AbstractFieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.reflect.primitive.CharacterGetter;
import org.sfm.reflect.primitive.CharacterSetter;

public class CharacterFieldMapper<S, T> extends AbstractFieldMapper<S, T> {

	private final CharacterGetter<S> getter;
	private final CharacterSetter<T> setter;
	
 	public CharacterFieldMapper(String name, CharacterGetter<S> getter, CharacterSetter<T> setter, FieldMapperErrorHandler errorHandler) {
 		super(name, errorHandler);
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	protected void mapUnsafe(S source, T target) throws Exception {
		setter.setCharacter(target, getter.getCharacter(source));
	}
}
