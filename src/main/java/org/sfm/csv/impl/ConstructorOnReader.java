package org.sfm.csv.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.sfm.csv.CellValueReader;

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
			throw new ParsingException(e);
		}
	}

}
