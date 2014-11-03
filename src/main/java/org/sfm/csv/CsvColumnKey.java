package org.sfm.csv;

import org.sfm.map.impl.FieldKey;

public class CsvColumnKey implements FieldKey<CsvColumnKey> {
	private final String name;
	private final int index;
	private final CsvColumnKey parent;
	
	public CsvColumnKey(String name, int index) {
		this(name, index, null);
	}
	public CsvColumnKey(String name, int index, CsvColumnKey parent) {
		super(); 
		this.name = name;
		this.index = index;
		this.parent = parent;
	}
	public String getName() {
		return name;
	}
	public int getIndex() {
		return index;
	}
	
	public CsvColumnKey alias(String name) {
		return new CsvColumnKey(name, index, this);
	}

	public boolean isAlias() {
		return parent != null;
	}
	
	public CsvColumnKey getParent() {
		return parent;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CsvColumnKey other = (CsvColumnKey) obj;
		if (index != other.index)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		return true;
	}
}
