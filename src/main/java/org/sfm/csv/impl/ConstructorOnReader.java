package org.sfm.csv.impl;

import org.sfm.csv.CellValueReader;
import org.sfm.utils.ErrorHelper;

import java.lang.reflect.Constructor;

public class ConstructorOnReader<T> implements CellValueReader<T> {
	private final Constructor<T> constructor;
	private final CellValueReader<?> innerReader;

	public ConstructorOnReader(Constructor<T> constructor,
			CellValueReader<?> innerReader) {
		this.constructor = constructor;
		this.innerReader = innerReader;
	}

	@Override
	public T read(char[] chars, int offset, int length,
			ParsingContext parsingContext) {
		try {
			return constructor.newInstance(innerReader.read(chars, offset, length, parsingContext));
		} catch (Exception e) {
            return ErrorHelper.rethrow(e);
		}
	}

    @Override
    public String toString() {
        return "ConstructorOnReader{" +
                "constructor=" + constructor +
                ", innerReader=" + innerReader +
                '}';
    }
}
