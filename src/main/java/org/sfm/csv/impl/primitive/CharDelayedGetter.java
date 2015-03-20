package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.AbstractTargetSetters;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.CharacterGetter;

public class CharDelayedGetter<T> implements CharacterGetter<AbstractTargetSetters<T>>, Getter<AbstractTargetSetters<T>, Character> {
	private final int index;
	
	public CharDelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public char getCharacter(AbstractTargetSetters<T> target) throws Exception {
		return ((CharDelayedCellSetter<T>)target.getDelayedCellSetter(index)).consumeCharacter();
	}

	@Override
	public Character get(AbstractTargetSetters<T> target) throws Exception {
		return getCharacter(target);
	}

    @Override
    public String toString() {
        return "CharDelayedGetter{" +
                "index=" + index +
                '}';
    }
}
