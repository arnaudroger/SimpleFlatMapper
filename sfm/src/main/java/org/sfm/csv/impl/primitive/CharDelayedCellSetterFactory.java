package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.BreakDetector;
import org.sfm.csv.impl.CsvMapperCellConsumer;
import org.sfm.csv.impl.DelayedCellSetter;
import org.sfm.csv.impl.DelayedCellSetterFactory;
import org.sfm.csv.impl.cellreader.CharCellValueReader;
import org.sfm.reflect.primitive.CharacterSetter;

public class CharDelayedCellSetterFactory<T> implements DelayedCellSetterFactory<T, Character> {

	private final CharacterSetter<T> setter;
	private final CharCellValueReader reader;

	public CharDelayedCellSetterFactory(CharacterSetter<T> setter, CharCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}

	@Override
	public DelayedCellSetter<T, Character> newCellSetter(BreakDetector breakDetector, CsvMapperCellConsumer<?>[] cellHandlers) {
		return new CharDelayedCellSetter<T>(setter, reader);
	}

    @Override
    public boolean hasSetter() {
        return setter != null;
    }

    @Override
    public String toString() {
        return "CharDelayedCellSetterFactory{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
