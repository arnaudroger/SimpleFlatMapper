package org.sfm.csv.primitive;

import org.sfm.csv.DelayedCellSetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.CharacterGetter;

public class CharDelayedGetter<T> implements CharacterGetter<DelayedCellSetter<T, ?>[]>, Getter<DelayedCellSetter<T, ?>[], Character> {
	private final int index;
	
	public CharDelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public char getCharacter(DelayedCellSetter<T, ?>[] target) throws Exception {
		return ((CharDelayedCellSetter<T>)target[index]).getCharacter();
	}

	@Override
	public Character get(DelayedCellSetter<T, ?>[] target) throws Exception {
		return Character.valueOf(getCharacter(target));
	}
}
