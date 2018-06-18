package org.simpleflatmapper.csv.impl;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.util.ErrorHelper;

public class InstantiatorOnReader<S, T> implements CellValueReader<T> {
	private final Instantiator<S, T> instantiator;
	private final CellValueReader<S> innerReader;

	public InstantiatorOnReader(Instantiator<S, T> constructor,
								CellValueReader<S> innerReader) {
		this.instantiator = constructor;
		this.innerReader = innerReader;
	}

	@Override
	public T read(char[] chars, int offset, int length,
			ParsingContext parsingContext) {
		try {
			return instantiator.newInstance(innerReader.read(chars, offset, length, parsingContext));
		} catch (Exception e) {
            return ErrorHelper.rethrow(e);
		}
	}

    @Override
    public String toString() {
        return "ConstructorOnReader{" +
                "constructor=" + instantiator +
                ", innerReader=" + innerReader +
                '}';
    }
}
