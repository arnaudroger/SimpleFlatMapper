package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.reflect.meta.AliasProvider;
import org.simpleflatmapper.reflect.meta.AliasProviderFactory;
import org.simpleflatmapper.reflect.meta.Table;

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

	@Override
	public Table getTable(Class<?> target) {
		Table table = Table.NULL;
		javax.persistence.Table annotation = target.getAnnotation(javax.persistence.Table.class);
		if (annotation != null) {
			table = new Table(annotation.catalog(), annotation.schema(), annotation.name());
		}
		return table;
	}

	public static void registers() {
		if (_isJpaPresent()) {
			AliasProviderFactory.register(new JpaAliasProvider());
		}
	}

	private static boolean _isJpaPresent() {
		try {
			return Column.class != null;
		} catch(Throwable e) {
			return false;
		}
	}
}
