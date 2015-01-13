package org.sfm.querydsl;

import com.mysema.query.types.Expression;
import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.PathType;
import org.sfm.map.MappingException;
import org.sfm.map.impl.FieldKey;

public class TupleElementKey implements FieldKey<TupleElementKey> {
	private final Expression<?> expression;
	private final int index;
	private final String name;
	public TupleElementKey(Expression<?> expression, int index) {
		if (expression instanceof Path<?>) {
			@SuppressWarnings("rawtypes")
			PathMetadata<?> metadata = ((Path) expression).getMetadata();
			if (metadata.getPathType() == PathType.PROPERTY) {
				name = metadata.getExpression().toString();
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
}
