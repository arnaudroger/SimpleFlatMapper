package org.sfm.querydsl;

import com.mysema.query.types.Expression;
import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.PathType;
import org.sfm.map.MappingException;
import org.sfm.map.FieldKey;

public class TupleElementKey implements FieldKey<TupleElementKey> {
	private final Expression<?> expression;
	private final int index;
	private final String name;
	public TupleElementKey(Expression<?> expression, int index) {
		if (expression instanceof Path<?>) {
			@SuppressWarnings("rawtypes")
			PathMetadata<?> metadata = ((Path) expression).getMetadata();
			if (metadata.getPathType() == PathType.PROPERTY) {
				name = metadata.getElement().toString();
			} else {
				throw new MappingException("Unexpected expression " + expression);
			}
		}  else {
			throw new MappingException("Unexpected expression " + expression);
		}
		this.expression = expression;
		this.index = index;
	}
	public Expression<?> getExpression() {
		return expression;
	}
	public int getIndex() {
		return index;
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
	public TupleElementKey alias(String alias) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return "TupleElementKey{" +
				"expression=" + expression +
				", index=" + index +
				", name='" + name + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TupleElementKey that = (TupleElementKey) o;

		if (index != that.index) return false;
		if (!expression.equals(that.expression)) return false;
		return name.equals(that.name);

	}

	@Override
	public int hashCode() {
		int result = expression.hashCode();
		result = 31 * result + index;
		result = 31 * result + name.hashCode();
		return result;
	}
}
