package org.sfm.csv;

import org.sfm.map.FieldKey;

public final class CsvColumnKey extends FieldKey<CsvColumnKey> {

	public CsvColumnKey(String name, int index) {
		super(name, index);
	}
	public CsvColumnKey(String name, int index, CsvColumnKey parent) {
		super(name, index, parent);
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
