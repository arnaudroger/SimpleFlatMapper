package org.sfm.reflect.meta;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class JpaAliasProvider implements AliasProvider {

	@Override
	public String getAliasForMethod(Method method) {
		String alias = null;
		Column col = method.getAnnotation(Column.class);
		if (col != null) {
			alias = col.name();
		}
		return alias;
	}

	@Override
	public String getAliasForField(Field field) {
		String alias = null;
		Column col = field.getAnnotation(Column.class);
		if (col != null) {
			alias = col.name();
		}
		return alias;
	}

}
