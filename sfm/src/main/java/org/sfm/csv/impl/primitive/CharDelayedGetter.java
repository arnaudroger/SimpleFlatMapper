package org.sfm.csv.impl.primitive;

import org.sfm.csv.mapper.CsvMapperCellHandler;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.CharacterGetter;

public class CharDelayedGetter<T> implements CharacterGetter<CsvMapperCellHandler<T>>, Getter<CsvMapperCellHandler<T>, Character> {
	private final int index;
	
	public CharDelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public char getCharacter(CsvMapperCellHandler<T> target) throws Exception {
		return ((CharDelayedCellSetter<T>)target.getDelayedCellSetter(index)).consumeCharacter();
	}

	@Override
	public Character get(CsvMapperCellHandler<T> target) throws Exception {
		return getCharacter(target);
	}

    @Override
    public String toString() {
        return "CharDelayedGetter{" +
                "index=" + index +
                '}';
    }
}
