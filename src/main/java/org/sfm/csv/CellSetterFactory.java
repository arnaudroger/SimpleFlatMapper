package org.sfm.csv;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.sfm.csv.cell.BooleanCellValueReader;
import org.sfm.csv.cell.ByteCellValueReader;
import org.sfm.csv.cell.CellSetterImpl;
import org.sfm.csv.cell.CharCellValueReader;
import org.sfm.csv.cell.DateCellValueReader;
import org.sfm.csv.cell.DelayedCellSetterImpl;
import org.sfm.csv.cell.DoubleCellValueReader;
import org.sfm.csv.cell.EnumCellValueReader;
import org.sfm.csv.cell.FloatCellValueReader;
import org.sfm.csv.cell.IntegerCellValueReader;
import org.sfm.csv.cell.LongCellValueReader;
import org.sfm.csv.cell.ParsingException;
import org.sfm.csv.cell.ShortCellValueReader;
import org.sfm.csv.cell.StringCellValueReader;
import org.sfm.reflect.Setter;
import org.sfm.reflect.TypeHelper;

public class CellSetterFactory {

	@SuppressWarnings("serial")
	private static final Map<Class<?>, CellValueReader<?>> transformers = new HashMap<Class<?>, CellValueReader<?>>(){{
		put(boolean.class,   new BooleanCellValueReader());
		put(Boolean.class,   new BooleanCellValueReader());
		put(byte.class,      new ByteCellValueReader());
		put(Byte.class,      new ByteCellValueReader());
		put(char.class,      new CharCellValueReader());
		put(Character.class, new CharCellValueReader());
		put(short.class,     new ShortCellValueReader());
		put(Short.class,     new ShortCellValueReader());
		put(Integer.class,   new IntegerCellValueReader());
		put(int.class,       new IntegerCellValueReader());
		put(Long.class,      new LongCellValueReader());
		put(long.class,      new LongCellValueReader());
		put(Float.class,     new FloatCellValueReader());
		put(float.class,     new FloatCellValueReader());
		put(Double.class,    new DoubleCellValueReader());
		put(double.class,    new DoubleCellValueReader());
		
		put(String.class,    new StringCellValueReader());
		put(Date.class,      new DateCellValueReader());
	}};
	public <T,P> CellSetter<T> getCellSetter(Setter<T, P> setter) {
		return new CellSetterImpl<T, P>(getReaderForSetter(setter), setter) ;
	}

	public <T, P> CellValueReader<P> getReaderForSetter(Setter<T, P> setter) {
		Class<P> propertyType = TypeHelper.toClass(setter.getPropertyType());
		CellValueReader<P> reader = getReader(propertyType);
		return reader;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <P> CellValueReader<P> getReader(Class<P> propertyType) {
		CellValueReader<P> reader = null;
		if (Enum.class.isAssignableFrom(propertyType)) {
			reader = new EnumCellValueReader(propertyType);
		} else {
			reader = getCellValueTransformer(propertyType);
		}
		if (reader == null) {
			throw new ParsingException("No cell reader for " + propertyType);
		}
		return reader;
	}

	@SuppressWarnings("unchecked")
	private <P> CellValueReader<P> getCellValueTransformer(Class<P> propertyType) {
		return (CellValueReader<P>) transformers.get(propertyType);
	}

	public <T, P> DelayedCellSetter<T, P> getDelayedCellSetter(
			Setter<T, P> setter) {
		return new DelayedCellSetterImpl<T, P>(getReaderForSetter(setter), setter);
	}
	@SuppressWarnings("unchecked")
	public <T, P> DelayedCellSetter<T, P> getDelayedCellSetter(Type type) {
		return new DelayedCellSetterImpl<T, P>(getReader((Class<P>)TypeHelper.toClass(type)), null);
	}
}
