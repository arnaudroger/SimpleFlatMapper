package org.sfm.jooq.conv;

import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.jdbc.ResultSetGetterFactory;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;
import org.sfm.utils.conv.*;

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
		final FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> identity = FieldMapperColumnDefinition.identity();
		Getter<ResultSet, E> elementGetter = new ResultSetGetterFactory().newGetter(eltType, new JdbcColumnKey("elt", 2), identity);
		if (elementGetter == null) return null;
		return (Converter<F, P>)new ArrayConverter<E>((Class<E>) TypeHelper.toClass(eltType), elementGetter);
	}
	@SuppressWarnings("unchecked")
	private static <F, P, E> Converter<F, P> newArrayToListConverter(Type eltType) {
		final FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> identity = FieldMapperColumnDefinition.identity();
		Getter<ResultSet, E> elementGetter = new ResultSetGetterFactory().newGetter(eltType, new JdbcColumnKey("elt", 2), identity);
		if (elementGetter == null) return null;
		return (Converter<F, P>)new ArrayToListConverter<E>(elementGetter);
	}
}
