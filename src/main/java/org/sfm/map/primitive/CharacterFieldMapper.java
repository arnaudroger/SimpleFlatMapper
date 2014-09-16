package org.sfm.map.primitive;

import org.sfm.map.AbstractFieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.reflect.primitive.CharacterGetter;
import org.sfm.reflect.primitive.CharacterSetter;

public final class CharacterFieldMapper<S, T, K> extends AbstractFieldMapper<S, T, K> {

	private final CharacterGetter<S> getter;
	private final CharacterSetter<T> setter;
	
 	public CharacterFieldMapper(final K key, final CharacterGetter<S> getter, final CharacterSetter<T> setter, final FieldMapperErrorHandler<K> errorHandler) {
 		super(key, errorHandler);
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	protected void mapUnsafe(final S source, final T target) throws Exception {
		setter.setCharacter(target, getter.getCharacter(source));
	}
}
