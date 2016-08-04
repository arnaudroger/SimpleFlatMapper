package org.simpleflatmapper.core.map;

import org.simpleflatmapper.util.Asserts;

public abstract class FieldKey<K extends FieldKey<K>> {

	protected final String name;
	protected final int index;
	protected final K parent;

    protected FieldKey(String name, int index) {
        this.name = Asserts.requireNonNull("name", name);
        this.index = index;
        this.parent = null;
    }

    protected FieldKey(String name, int index, K parent) {
		this.name = Asserts.requireNonNull("name", name);
		this.index = index;
		this.parent = parent;
	}

	public final K getParent() {
		return parent;
	}

	public final String getName() {
		return name;
	}
	public final int getIndex(){
		return index;
	}

    public final boolean isAlias() {
        return parent != null;
    }


    @Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FieldKey<?> fieldKey = (FieldKey<?>) o;

		if (index != fieldKey.index) return false;
		return name.equals(fieldKey.name);

	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + index;
		return result;
	}

	public abstract K alias(String alias);
}
