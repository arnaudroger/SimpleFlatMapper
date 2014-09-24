package org.sfm.csv.primitive;

import org.sfm.csv.DelayedCellSetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.CharacterGetter;

@SuppressWarnings("rawtypes")
public class CharDelayedGetter implements CharacterGetter<DelayedCellSetter[]>, Getter<DelayedCellSetter[], Character> {
	private final int index;
	
	public CharDelayedGetter(int index) {
		this.index = index;
	}

	@Override
	public char getCharacter(DelayedCellSetter[] target) throws Exception {
		return ((CharDelayedCellSetter<?>)target[index]).getCharacter();
	}

	@Override
	public Character get(DelayedCellSetter[] target) throws Exception {
		return Character.valueOf(getCharacter(target));
	}
}
