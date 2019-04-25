package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.map.annotation.Column;
import org.simpleflatmapper.reflect.meta.AliasProvider;
import org.simpleflatmapper.reflect.meta.Table;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SfmAliasProvider implements AliasProvider {


	@SuppressWarnings("unchecked")
	public SfmAliasProvider() {
	}


	@Override
	public String getAliasForMethod(Method method) {
		String alias = null;
		Column col = method.getAnnotation(Column.class);
		if (col != null) {
			alias = col.value();
		}
		return alias;
	}


	@Override
	public String getAliasForField(Field field) {
		String alias = null;
		Column col = field.getAnnotation(Column.class);
		if (col != null) {
			alias = col.value();
		}
		return alias;
	}

	@Override
	public Table getTable(Class<?> target) {
		return  Table.NULL;
	}

}
