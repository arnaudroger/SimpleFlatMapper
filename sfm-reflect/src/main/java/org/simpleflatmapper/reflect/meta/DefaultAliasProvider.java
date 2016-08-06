package org.simpleflatmapper.reflect.meta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class DefaultAliasProvider implements AliasProvider {

	@Override
	public String getAliasForMethod(Method method) {
		return null;
	}

	@Override
	public String getAliasForField(Field field) {
		return null;
	}

	@Override
	public Table getTable(Class<?> target) {
		return Table.NULL;
	}
}
