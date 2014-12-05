package org.sfm.reflect;

import java.lang.reflect.Type;

public final class ConstructorParameter {
	private final String name;
	private final Type type;
	private final Type resolvedType;

	public ConstructorParameter(String name, Type type, Type resolvedType) {
		super();
		this.name = name;
		this.type = type;
		this.resolvedType = resolvedType;
	}

	public ConstructorParameter(String id, Type type) {
		this(id, type, type);
	}

	public String getName() {
		return name;
	}
	public Type getType() {
		return type;
	}

	public Type getResolvedType() {
		return resolvedType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		ConstructorParameter other = (ConstructorParameter) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
