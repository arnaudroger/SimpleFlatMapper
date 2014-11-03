package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.DelayedCellSetter;
import org.sfm.csv.impl.DelayedCellSetterFactory;
import org.sfm.reflect.primitive.CharacterSetter;

public class CharDelayedCellSetterFactory<T> implements DelayedCellSetterFactory<T, Character> {

	private final CharacterSetter<T> setter;
	
	public CharDelayedCellSetterFactory(CharacterSetter<T> setter) {
		this.setter = setter;
	}

	@Override
	public DelayedCellSetter<T, Character> newCellSetter() {
		return new CharDelayedCellSetter<T>(setter);
	}
}
