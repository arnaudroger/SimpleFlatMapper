package org.simpleflatmapper.csv;

import org.simpleflatmapper.map.FieldKey;

import java.lang.reflect.Type;

public final class CsvColumnKey extends FieldKey<CsvColumnKey> {

	public CsvColumnKey(String name, int index) {
		super(name, index);
	}
	public CsvColumnKey(String name, int index, CsvColumnKey parent) {
		super(name, index, parent);
	}

	@Override
	public Type getType(Type targetType) {
		return CharSequence.class;
	}

	public CsvColumnKey alias(String name) {
		return new CsvColumnKey(name, index, this);
	}

	@Override
	public String toString() {
		return "CsvColumnKey{" +
				"name='" + name + '\'' +
				", index=" + index +
				'}';
	}
}
