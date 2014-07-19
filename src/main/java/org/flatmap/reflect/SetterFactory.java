package org.flatmap.reflect;

import org.flatmap.reflect.primitive.BooleanSetter;
import org.flatmap.reflect.primitive.ByteSetter;
import org.flatmap.reflect.primitive.CharacterSetter;
import org.flatmap.reflect.primitive.DoubleSetter;
import org.flatmap.reflect.primitive.FloatSetter;
import org.flatmap.reflect.primitive.IntSetter;
import org.flatmap.reflect.primitive.LongSetter;
import org.flatmap.reflect.primitive.ShortSetter;

public interface SetterFactory {
	<T, P, C extends T> Setter<T, P> getSetter(Class<C> target, String property);
	
	<T, P> BooleanSetter<T> toBooleanSetter(Setter<T, P> setter);
	<T, P> ByteSetter<T> toByteSetter(Setter<T, P> setter);
	<T, P> CharacterSetter<T> toCharacterSetter(Setter<T, P> setter);
	<T, P> ShortSetter<T> toShortSetter(Setter<T, P> setter);
	<T, P> IntSetter<T> toIntSetter(Setter<T, P> setter);
	<T, P> LongSetter<T> toLongSetter(Setter<T, P> setter);
	<T, P> FloatSetter<T> toFloatSetter(Setter<T, P> setter);
	<T, P> DoubleSetter<T> toDoubleSetter(Setter<T, P> setter);
}
