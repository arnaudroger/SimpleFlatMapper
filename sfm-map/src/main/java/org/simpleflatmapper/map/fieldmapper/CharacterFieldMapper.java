package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.getter.CharacterContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetterAdapter;
import org.simpleflatmapper.map.setter.CharacterContextualSetter;
import org.simpleflatmapper.map.setter.ContextualSetterAdapter;
import org.simpleflatmapper.reflect.primitive.CharacterGetter;
import org.simpleflatmapper.reflect.primitive.CharacterSetter;

public final class CharacterFieldMapper<S, T> implements FieldMapper<S, T> {

	private final CharacterContextualGetter<? super S> getter;
	private final CharacterContextualSetter<? super T> setter;
	
 	public CharacterFieldMapper(final CharacterContextualGetter<? super S> getter, final CharacterSetter<? super T> setter) {
		this.getter = getter;
		this.setter = ContextualSetterAdapter.of(setter);
	}

	public CharacterFieldMapper(final CharacterGetter<? super S> getter, final CharacterContextualSetter<? super T> setter) {
		this.getter = ContextualGetterAdapter.of(getter);
		this.setter = setter;
	}
	
	@Override
	public void mapTo(final S source, final T target, final MappingContext<? super S> mappingContext) throws Exception {
		setter.setCharacter(target, getter.getCharacter(source, mappingContext), mappingContext);
	}

    @Override
    public String toString() {
        return "CharacterFieldMapper{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
