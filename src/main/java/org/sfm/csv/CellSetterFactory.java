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
import org.sfm.csv.cell.DelayedCellSetterFactoryImpl;
import org.sfm.csv.cell.DoubleCellValueReader;
import org.sfm.csv.cell.EnumCellValueReader;
import org.sfm.csv.cell.FloatCellValueReader;
import org.sfm.csv.cell.IntegerCellValueReader;
import org.sfm.csv.cell.LongCellValueReader;
import org.sfm.csv.cell.ParsingException;
import org.sfm.csv.cell.ShortCellValueReader;
import org.sfm.csv.cell.StringCellValueReader;
import org.sfm.csv.primitive.BooleanCellSetter;
import org.sfm.csv.primitive.BooleanDelayedCellSetterFactory;
import org.sfm.csv.primitive.ByteCellSetter;
import org.sfm.csv.primitive.ByteDelayedCellSetterFactory;
import org.sfm.csv.primitive.CharCellSetter;
import org.sfm.csv.primitive.CharDelayedCellSetterFactory;
import org.sfm.csv.primitive.DoubleCellSetter;
import org.sfm.csv.primitive.DoubleDelayedCellSetterFactory;
import org.sfm.csv.primitive.FloatCellSetter;
import org.sfm.csv.primitive.FloatDelayedCellSetterFactory;
import org.sfm.csv.primitive.IntCellSetter;
import org.sfm.csv.primitive.IntDelayedCellSetterFactory;
import org.sfm.csv.primitive.LongCellSetter;
import org.sfm.csv.primitive.LongDelayedCellSetterFactory;
import org.sfm.csv.primitive.ShortCellSetter;
import org.sfm.csv.primitive.ShortDelayedCellSetterFactory;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;
import org.sfm.reflect.TypeHelper;

public class CellSetterFactory {

	@SuppressWarnings("serial")
	private static final Map<Class<?>, CellValueReader<?>> transformers = new HashMap<Class<?>, CellValueReader<?>>(){{
		put(boolean.class,   new BooleanCellValueReader());
		put(byte.class,      new ByteCellValueReader());
		put(char.class, new CharCellValueReader());
		put(short.class,     new ShortCellValueReader());
		put(int.class,   new IntegerCellValueReader());
		put(long.class,      new LongCellValueReader());
		put(float.class,     new FloatCellValueReader());
		put(double.class,    new DoubleCellValueReader());
		put(Boolean.class,   new BooleanCellValueReader());
		put(Byte.class,      new ByteCellValueReader());
		put(Character.class, new CharCellValueReader());
		put(Short.class,     new ShortCellValueReader());
		put(Integer.class,   new IntegerCellValueReader());
		put(Long.class,      new LongCellValueReader());
		put(Float.class,     new FloatCellValueReader());
		put(Double.class,    new DoubleCellValueReader());
		put(String.class,    new StringCellValueReader());
	}};
	
	private Map<String, CellValueReader<?>> customReaders;
	
	public CellSetterFactory(Map<String, CellValueReader<?>> customReaders) {
		this.customReaders = customReaders;
	}

	public CellSetterFactory() {
		this(new HashMap<String, CellValueReader<?>>());
	}

	public <T,P> CellSetter<T> getCellSetter(Setter<T, P> setter, int index, String columnName) {
		Class<?> propertyClass = TypeHelper.toClass(setter.getPropertyType());
		
		if (propertyClass.isPrimitive()) {
			return getPrimitiveCellSetter(propertyClass, setter);
		}
		
		return new CellSetterImpl<T, P>(getReaderForSetter(setter, index, columnName), setter) ;
	}
	
	public <T,P> CellSetter<T> getPrimitiveCellSetter(Class<?> clazz, Setter<T, P> setter) {
		if (boolean.class.equals(clazz)) {
			return new BooleanCellSetter<T>(SetterFactory.toBooleanSetter(setter));
		} else if (byte.class.equals(clazz)) {
			return new ByteCellSetter<T>(SetterFactory.toByteSetter(setter));
		} else if (char.class.equals(clazz)) {
			return new CharCellSetter<T>(SetterFactory.toCharacterSetter(setter));
		} else if (short.class.equals(clazz)) {
			return new ShortCellSetter<T>(SetterFactory.toShortSetter(setter));
		} else if (int.class.equals(clazz)) {
			return new IntCellSetter<T>(SetterFactory.toIntSetter(setter));
		} else if (long.class.equals(clazz)) {
			return new LongCellSetter<T>(SetterFactory.toLongSetter(setter));
		} else if (float.class.equals(clazz)) {
			return new FloatCellSetter<T>(SetterFactory.toFloatSetter(setter));
		} else if (double.class.equals(clazz)) {
			return new DoubleCellSetter<T>(SetterFactory.toDoubleSetter(setter));
		} 
		throw new IllegalArgumentException("Invalid primitive type " + clazz);
	}
	
	@SuppressWarnings("unchecked")
	public <T,P> DelayedCellSetterFactory<T, P> getPrimitiveDelayedCellSetter(Class<?> clazz, Setter<T, P> setter) {
		if (boolean.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new BooleanDelayedCellSetterFactory<T>(SetterFactory.toBooleanSetter(setter));
		} else if (byte.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new ByteDelayedCellSetterFactory<T>(SetterFactory.toByteSetter(setter));
		} else if (char.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new CharDelayedCellSetterFactory<T>(SetterFactory.toCharacterSetter(setter));
		} else if (short.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new ShortDelayedCellSetterFactory<T>(SetterFactory.toShortSetter(setter));
		} else if (int.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new IntDelayedCellSetterFactory<T>(SetterFactory.toIntSetter(setter));
		} else if (long.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new LongDelayedCellSetterFactory<T>(SetterFactory.toLongSetter(setter));
		} else if (float.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new FloatDelayedCellSetterFactory<T>(SetterFactory.toFloatSetter(setter));
		} else if (double.class.equals(clazz)) {
			return (DelayedCellSetterFactory<T, P>) new DoubleDelayedCellSetterFactory<T>(SetterFactory.toDoubleSetter(setter));
		} 
		throw new IllegalArgumentException("Invalid primitive type " + clazz);
	}

	private <T, P> CellValueReader<P> getReaderForSetter(Setter<T, P> setter, int index, String colunn) {
		Class<P> propertyType = TypeHelper.toClass(setter.getPropertyType());
		CellValueReader<P> reader = getReader(propertyType, index, colunn);
		return reader;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <P> CellValueReader<P> getReader(Class<P> propertyType, int index, String colunn) {
		CellValueReader<P> reader = null;
		
		reader = (CellValueReader<P>) customReaders.get(colunn);
		if (reader == null) {
			if (Date.class.isAssignableFrom(propertyType)) {
				reader = (CellValueReader<P>) new DateCellValueReader(index);
			} else if (Enum.class.isAssignableFrom(propertyType)) {
				reader = new EnumCellValueReader(propertyType);
			} else {
				reader = getCellValueTransformer(propertyType);
			}
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

	public <T, P> DelayedCellSetterFactory<T, P> getDelayedCellSetter(Setter<T, P> setter, int index, String columnName) {
		Class<?> propertyClass = TypeHelper.toClass(setter.getPropertyType());
		
		if (propertyClass.isPrimitive()) {
			return getPrimitiveDelayedCellSetter(propertyClass, setter);
		}
		
		return new DelayedCellSetterFactoryImpl<T, P>(getReaderForSetter(setter, index, columnName), setter);
	}
	@SuppressWarnings("unchecked")
	public <T, P> DelayedCellSetterFactory<T, P> getDelayedCellSetter(Type type, int index, String columnName) {
		Class<?> propertyClass = TypeHelper.toClass(type);
		
		if (propertyClass.isPrimitive()) {
			return getPrimitiveDelayedCellSetter(propertyClass, null);
		}
		
		return new DelayedCellSetterFactoryImpl<T, P>(getReader((Class<P>)TypeHelper.toClass(type), index, columnName), null);
	}
}
