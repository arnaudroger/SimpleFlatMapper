package org.sfm.csv.impl;

import org.sfm.csv.CellValueReader;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.ErrorHelper;

public class InstantiatorOnReader<S, T> implements CellValueReader<T> {
	private final Instantiator<S, T> instantiator;
	private final CellValueReader<S> innerReader;

	public InstantiatorOnReader(Instantiator<S, T> constructor,
								CellValueReader<S> innerReader) {
		this.instantiator = constructor;
		this.innerReader = innerReader;
	}

	@Override
	public T read(CharSequence value,
			ParsingContext parsingContext) {
		try {
			return instantiator.newInstance(innerReader.read(value, parsingContext));
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
