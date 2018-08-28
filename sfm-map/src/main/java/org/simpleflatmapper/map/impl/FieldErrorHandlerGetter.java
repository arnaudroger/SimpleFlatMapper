package org.simpleflatmapper.map.impl;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapperErrorHandler;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.fieldmapper.BooleanFieldMapperGetter;
import org.simpleflatmapper.map.fieldmapper.ByteFieldMapperGetter;
import org.simpleflatmapper.map.fieldmapper.CharacterFieldMapperGetter;
import org.simpleflatmapper.map.fieldmapper.DoubleFieldMapperGetter;
import org.simpleflatmapper.map.fieldmapper.FieldMapperGetter;
import org.simpleflatmapper.map.fieldmapper.FloatFieldMapperGetter;
import org.simpleflatmapper.map.fieldmapper.IntFieldMapperGetter;
import org.simpleflatmapper.map.fieldmapper.LongFieldMapperGetter;
import org.simpleflatmapper.map.fieldmapper.ShortFieldMapperGetter;


public class FieldErrorHandlerGetter<S, T, K> implements FieldMapperGetter<S, T> {

	public final FieldMapperGetter<? super S, ? extends T> delegate;
	public final FieldMapperErrorHandler<? super K> errorHandler;
	public final K key;
	
	public FieldErrorHandlerGetter(K key, FieldMapperGetter<? super S, ? extends T> delegate,
                                   FieldMapperErrorHandler<? super K> errorHandler) {
		super();
		this.key = key;
		this.delegate = delegate;
		this.errorHandler = errorHandler;
	}

	@Override
	public T get(S source, MappingContext<?> context)  {
		try {
			return delegate.get(source, context);
		} catch(Exception e) {
			errorHandler.errorMappingField(key, source, null, e);
			return null;
		}
	}

    @Override
    public String toString() {
        return "FieldErrorHandlerMapper{delegate=" + delegate + '}';
	}


	public  static <S, T, K extends FieldKey<K>> FieldMapperGetter<S, T> of(K key, FieldMapperGetter<? super S, ? extends T> delegate,
																	  FieldMapperErrorHandler<? super K> errorHandler) {

		if (delegate instanceof BooleanFieldMapperGetter) {
			return new BooleanFieldErrorHandlerGetter<S, T, K>(key, delegate, errorHandler);
		}
		if (delegate instanceof ByteFieldMapperGetter) {
			return new ByteFieldErrorHandlerGetter<S, T, K>(key, delegate, errorHandler);
		}
		if (delegate instanceof CharacterFieldMapperGetter) {
			return new CharFieldErrorHandlerGetter<S, T, K>(key, delegate, errorHandler);
		}
		if (delegate instanceof ShortFieldMapperGetter) {
			return new ShortFieldErrorHandlerGetter<S, T, K>(key, delegate, errorHandler);
		}
		if (delegate instanceof IntFieldMapperGetter) {
			return new IntFieldErrorHandlerGetter<S, T, K>(key, delegate, errorHandler);
		}
		if (delegate instanceof LongFieldMapperGetter) {
			return new LongFieldErrorHandlerGetter<S, T, K>(key, delegate, errorHandler);
		}
		if (delegate instanceof FloatFieldMapperGetter) {
			return new FloatFieldErrorHandlerGetter<S, T, K>(key, delegate, errorHandler);
		}
		if (delegate instanceof DoubleFieldMapperGetter) {
			return new DoubleFieldErrorHandlerGetter<S, T, K>(key, delegate, errorHandler);
		}
		
		
		return new FieldErrorHandlerGetter<S, T, K>(key, delegate, errorHandler);
	}


	private static class BooleanFieldErrorHandlerGetter<S, T, K> extends FieldErrorHandlerGetter<S, T, K> implements BooleanFieldMapperGetter<T> {

		private BooleanFieldMapperGetter<T> pGetter;

		public BooleanFieldErrorHandlerGetter(K key, FieldMapperGetter<? super S, ? extends T> delegate, FieldMapperErrorHandler<? super K> errorHandler) {
			super(key, delegate, errorHandler);
			this.pGetter = (BooleanFieldMapperGetter<T>) delegate;
		}

		@Override
		public boolean getBoolean(T source, MappingContext<?> mappingContext) throws Exception {
			try {
				return pGetter.getBoolean(source, mappingContext);
			} catch(Exception e) {
				errorHandler.errorMappingField(key, source, null, e);
				return false;
			}
		}
	}
	
	private static class ByteFieldErrorHandlerGetter<S, T, K> extends FieldErrorHandlerGetter<S, T, K> implements ByteFieldMapperGetter<T> {

		private ByteFieldMapperGetter<T> pGetter;
		
		public ByteFieldErrorHandlerGetter(K key, FieldMapperGetter<? super S, ? extends T> delegate, FieldMapperErrorHandler<? super K> errorHandler) {
			super(key, delegate, errorHandler);
			this.pGetter = (ByteFieldMapperGetter<T>) delegate;
		}

		@Override
		public byte getByte(T source, MappingContext<?> mappingContext) throws Exception {
			try {
				return pGetter.getByte(source, mappingContext);
			} catch(Exception e) {
				errorHandler.errorMappingField(key, source, null, e);
				return 0;
			}
		}
	}

	private static class CharFieldErrorHandlerGetter<S, T, K> extends FieldErrorHandlerGetter<S, T, K> implements CharacterFieldMapperGetter<T> {

		private CharacterFieldMapperGetter<T> pGetter;

		public CharFieldErrorHandlerGetter(K key, FieldMapperGetter<? super S, ? extends T> delegate, FieldMapperErrorHandler<? super K> errorHandler) {
			super(key, delegate, errorHandler);
			this.pGetter = (CharacterFieldMapperGetter<T>) delegate;
		}

		@Override
		public char getCharacter(T source, MappingContext<?> mappingContext) throws Exception {
			try {
				return pGetter.getCharacter(source, mappingContext);
			} catch(Exception e) {
				errorHandler.errorMappingField(key, source, null, e);
				return 0;
			}
		}
	}

	private static class ShortFieldErrorHandlerGetter<S, T, K> extends FieldErrorHandlerGetter<S, T, K> implements ShortFieldMapperGetter<T> {

		private ShortFieldMapperGetter<T> pGetter;

		public ShortFieldErrorHandlerGetter(K key, FieldMapperGetter<? super S, ? extends T> delegate, FieldMapperErrorHandler<? super K> errorHandler) {
			super(key, delegate, errorHandler);
			this.pGetter = (ShortFieldMapperGetter<T>) delegate;
		}

		@Override
		public short getShort(T source, MappingContext<?> mappingContext) throws Exception {
			try {
				return pGetter.getShort(source, mappingContext);
			} catch(Exception e) {
				errorHandler.errorMappingField(key, source, null, e);
				return 0;
			}
		}
	}

	private static class IntFieldErrorHandlerGetter<S, T, K> extends FieldErrorHandlerGetter<S, T, K> implements IntFieldMapperGetter<T> {

		private IntFieldMapperGetter<T> pGetter;

		public IntFieldErrorHandlerGetter(K key, FieldMapperGetter<? super S, ? extends T> delegate, FieldMapperErrorHandler<? super K> errorHandler) {
			super(key, delegate, errorHandler);
			this.pGetter = (IntFieldMapperGetter<T>) delegate;
		}

		@Override
		public int getInt(T source, MappingContext<?> mappingContext) throws Exception {
			try {
				return pGetter.getInt(source, mappingContext);
			} catch(Exception e) {
				errorHandler.errorMappingField(key, source, null, e);
				return 0;
			}
		}
	}

	private static class LongFieldErrorHandlerGetter<S, T, K> extends FieldErrorHandlerGetter<S, T, K> implements LongFieldMapperGetter<T> {

		private LongFieldMapperGetter<T> pGetter;

		public LongFieldErrorHandlerGetter(K key, FieldMapperGetter<? super S, ? extends T> delegate, FieldMapperErrorHandler<? super K> errorHandler) {
			super(key, delegate, errorHandler);
			this.pGetter = (LongFieldMapperGetter<T>) delegate;
		}

		@Override
		public long getLong(T source, MappingContext<?> mappingContext) throws Exception {
			try {
				return pGetter.getLong(source, mappingContext);
			} catch(Exception e) {
				errorHandler.errorMappingField(key, source, null, e);
				return 0;
			}
		}
	}

	private static class FloatFieldErrorHandlerGetter<S, T, K> extends FieldErrorHandlerGetter<S, T, K> implements FloatFieldMapperGetter<T> {

		private FloatFieldMapperGetter<T> pGetter;

		public FloatFieldErrorHandlerGetter(K key, FieldMapperGetter<? super S, ? extends T> delegate, FieldMapperErrorHandler<? super K> errorHandler) {
			super(key, delegate, errorHandler);
			this.pGetter = (FloatFieldMapperGetter<T>) delegate;
		}

		@Override
		public float getFloat(T source, MappingContext<?> mappingContext) throws Exception {
			try {
				return pGetter.getFloat(source, mappingContext);
			} catch(Exception e) {
				errorHandler.errorMappingField(key, source, null, e);
				return 0;
			}
		}
	}

	private static class DoubleFieldErrorHandlerGetter<S, T, K> extends FieldErrorHandlerGetter<S, T, K> implements DoubleFieldMapperGetter<T> {

		private DoubleFieldMapperGetter<T> pGetter;

		public DoubleFieldErrorHandlerGetter(K key, FieldMapperGetter<? super S, ? extends T> delegate, FieldMapperErrorHandler<? super K> errorHandler) {
			super(key, delegate, errorHandler);
			this.pGetter = (DoubleFieldMapperGetter<T>) delegate;
		}

		@Override
		public double getDouble(T source, MappingContext<?> mappingContext) throws Exception {
			try {
				return pGetter.getDouble(source, mappingContext);
			} catch(Exception e) {
				errorHandler.errorMappingField(key, source, null, e);
				return 0;
			}
		}
	}
}
