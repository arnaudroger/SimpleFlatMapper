package org.simpleflatmapper.reflect;

import java.lang.reflect.Type;

public final class Parameter {
	private final int index;
	private final String name;
	private final Class<?> type;
	private final Type genericType;

	public Parameter(int index, String name, Class<?> type, Type genericType) {
		super();
		this.index = index;
		this.name = name;
		this.type = type;
		this.genericType = genericType;
	}

	public Parameter(int index, String id, Class<?> type) {
		this(index, id, type, type);
	}

	public String getName() {
		return name;
	}

	public Class<?> getType() {
		return type;
	}

	public Type getGenericType() {
		return genericType;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Parameter parameter = (Parameter) o;

		if (index != parameter.index) return false;
		if (name != null ? !name.equals(parameter.name) : parameter.name != null) return false;
		if (!type.equals(parameter.type)) return false;
		return !(genericType != null ? !genericType.equals(parameter.genericType) : parameter.genericType != null);

	}

	@Override
	public int hashCode() {
		int result = index;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + type.hashCode();
		result = 31 * result + (genericType != null ? genericType.hashCode() : 0);
		return result;
	}

	@Override
    public String toString() {
        return "Parameter{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", resolvedType=" + genericType +
                '}';
    }
}
