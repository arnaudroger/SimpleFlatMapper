package org.simpleflatmapper.jooq.conv;

import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.ResultSetGetterFactory;
import org.simpleflatmapper.map.column.FieldMapperColumnDefinition;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.List;

public class JooqConverterFactory {

	public static <P, F> Converter<F, P> getConverter(Class<F> inType, Class<P> outType) {
		return getConverter(inType, (Type)outType);
	}
	@SuppressWarnings("unchecked")
	public static <P, F> Converter<F, P> getConverter(Class<F> inType, Type outType) {
		if (TypeHelper.isArray(outType)) {
			return  newArrayConverter(TypeHelper.getComponentTypeOfListOrArray(outType));
		} else if (TypeHelper.isAssignable(List.class, outType)) {
			return  newArrayToListConverter(TypeHelper.getComponentTypeOfListOrArray(outType));
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static <F, P, E> Converter<F, P> newArrayConverter(Type eltType) {
		final FieldMapperColumnDefinition<JdbcColumnKey> identity = FieldMapperColumnDefinition.identity();
		Getter<ResultSet, E> elementGetter = new ResultSetGetterFactory().newGetter(eltType, new JdbcColumnKey("elt", 2), identity);
		if (elementGetter == null) return null;
		return (Converter<F, P>)new ArrayConverter<E>((Class<E>) TypeHelper.toClass(eltType), elementGetter);
	}
	@SuppressWarnings("unchecked")
	private static <F, P, E> Converter<F, P> newArrayToListConverter(Type eltType) {
		final FieldMapperColumnDefinition<JdbcColumnKey> identity = FieldMapperColumnDefinition.identity();
		Getter<ResultSet, E> elementGetter = new ResultSetGetterFactory().newGetter(eltType, new JdbcColumnKey("elt", 2), identity);
		if (elementGetter == null) return null;
		return (Converter<F, P>)new ArrayToListConverter<E>(elementGetter);
	}
}
