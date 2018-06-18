package org.simpleflatmapper.csv.impl.primitive;

import org.simpleflatmapper.csv.mapper.CsvMapperCellHandler;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.CharacterGetter;

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
