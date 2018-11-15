package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.reflect.meta.AliasProvider;
import org.simpleflatmapper.reflect.meta.Table;
import org.simpleflatmapper.util.ErrorHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class JpaAliasProvider implements AliasProvider {

	private final Class<? extends Annotation> columnClass;
	private final Method columnName;
	private final Class<? extends Annotation> tableClass;
	private final Method tableName;
	private final Method tableSchema;
	private final Method tableCatalog;

	@SuppressWarnings("unchecked")
	public JpaAliasProvider() {
		Class<? extends Annotation> columnClass = null;
		Method columnName = null;
		Class<? extends Annotation> tableClass = null;
		Method tableName = null;
		Method tableSchema = null;
		Method tableCatalog = null;
		try {
			columnClass = (Class<? extends Annotation>) Class.forName("javax.persistence.Column");
			columnName = columnClass.getDeclaredMethod("name");
			tableClass = (Class<? extends Annotation>) Class.forName("javax.persistence.Table");
			tableName = tableClass.getDeclaredMethod("name");
			tableSchema = tableClass.getDeclaredMethod("schema");
			tableCatalog = tableClass.getDeclaredMethod("catalog");
		} catch(Exception e) {
			ErrorHelper.rethrow(e);
		}

		this.columnClass = columnClass;
		this.columnName = columnName;
		this.tableClass = tableClass;
		this.tableName = tableName;
		this.tableSchema = tableSchema;
		this.tableCatalog = tableCatalog;
	}


	@Override
	public String getAliasForMethod(Method method) {
		String alias = null;
		Object col = method.getAnnotation(columnClass);
		if (col != null) {
			alias = getColumnName(col);
		}
		return alias;
	}

	private String getColumnName(Object col) {
		return getString(col, columnName);
	}

	private String getTableName(Object col) {
		return getString(col, tableName);
	}

	private String getTableSchema(Object col) {
		return getString(col, tableSchema);
	}

	private String getTableCatalog(Object col) {
		return getString(col, tableCatalog);
	}

	private String getString(Object col, Method method) {
		try {
			return (String) method.invoke(col);
        } catch (Exception e) {
			return ErrorHelper.rethrow(e);
        }
	}

	@Override
	public String getAliasForField(Field field) {
		String alias = null;
		Object col = field.getAnnotation(columnClass);
		if (col != null) {
			alias = getColumnName(col);
		}
		return alias;
	}

	@Override
	public Table getTable(Class<?> target) {
		Table table = Table.NULL;
		Object annotation = target.getAnnotation(tableClass);
		if (annotation != null) {
			table = new Table(getTableCatalog(annotation), getTableSchema(annotation), getTableName(annotation));
		}
		return table;
	}

}
