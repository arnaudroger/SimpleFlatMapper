package org.sfm.map.impl.fieldmapper;

import org.sfm.map.FieldMapper;
import org.sfm.map.MappingContext;
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
	public void mapTo(final S source, final T target, final MappingContext<S> mappingContext) throws Exception {
		setter.setCharacter(target, getter.getCharacter(source));
	}

    @Override
    public String toString() {
        return "CharacterFieldMapper{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
