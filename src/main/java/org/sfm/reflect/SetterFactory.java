package org.sfm.reflect;

import org.sfm.reflect.primitive.BooleanSetter;
import org.sfm.reflect.primitive.ByteSetter;
import org.sfm.reflect.primitive.CharacterSetter;
import org.sfm.reflect.primitive.DoubleSetter;
import org.sfm.reflect.primitive.FloatSetter;
import org.sfm.reflect.primitive.IntSetter;
import org.sfm.reflect.primitive.LongSetter;
import org.sfm.reflect.primitive.ShortSetter;

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
