package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.getter.CharacterContextualGetter;
import org.simpleflatmapper.reflect.primitive.CharacterSetter;

public final class CharacterConstantSourceFieldMapper<S, T> implements FieldMapper<S, T> {

	public final CharacterContextualGetter<? super S> getter;
	public final CharacterSetter<? super T> setter;
	
 	public CharacterConstantSourceFieldMapper(final CharacterContextualGetter<? super S> getter, final CharacterSetter<? super T> setter) {
		this.getter = getter;
		this.setter = setter;
	}
	
	@Override
	public void mapTo(final S source, final T target, final MappingContext<? super S> mappingContext) throws Exception {
		setter.setCharacter(target, getter.getCharacter(source, mappingContext));
	}

    @Override
    public String toString() {
        return "CharacterFieldMapper{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
