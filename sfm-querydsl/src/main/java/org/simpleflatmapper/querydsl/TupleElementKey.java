package org.simpleflatmapper.querydsl;

import com.mysema.query.types.Expression;
import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.PathType;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.FieldKey;

import java.lang.reflect.Type;

public final class TupleElementKey extends FieldKey<TupleElementKey> {
	private final Expression<?> expression;

	public TupleElementKey(String name, int index) {
		super(name, index);
		this.expression = null;
	}

	@Override
	public Type getType(Type targetType) {
		return expression.getType();
	}

	public TupleElementKey(Expression<?> expression, int index) {
		super(getName(expression), index);
		this.expression = expression;
	}

	private static String getName(Expression<?> expression) {
		if (expression instanceof Path<?>) {
			@SuppressWarnings("rawtypes")
			PathMetadata<?> metadata = ((Path) expression).getMetadata();
			if (metadata.getPathType() == PathType.PROPERTY) {
				return metadata.getElement().toString();
			} else {
				throw new MappingException("Unexpected expression " + expression);
			}
		}  else {
			throw new MappingException("Unexpected expression " + expression);
		}
	}

	public final Expression<?> getExpression() {
		return expression;
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


}
