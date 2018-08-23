package org.simpleflatmapper.map.impl;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapperErrorHandler;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.BooleanGetter;
import org.simpleflatmapper.reflect.primitive.ByteGetter;
import org.simpleflatmapper.reflect.primitive.CharacterGetter;
import org.simpleflatmapper.reflect.primitive.DoubleGetter;
import org.simpleflatmapper.reflect.primitive.FloatGetter;
import org.simpleflatmapper.reflect.primitive.IntGetter;
import org.simpleflatmapper.reflect.primitive.LongGetter;
import org.simpleflatmapper.reflect.primitive.ShortGetter;


public class FieldErrorHandlerGetter<S, T, K> implements Getter<S, T> {

	public final Getter<? super S, ? extends T> delegate;
	public final FieldMapperErrorHandler<? super K> errorHandler;
	public final K key;
	
	public FieldErrorHandlerGetter(K key, Getter<? super S, ? extends T> delegate,
                                   FieldMapperErrorHandler<? super K> errorHandler) {
		super();
		this.key = key;
		this.delegate = delegate;
		this.errorHandler = errorHandler;
	}

	@Override
	public T get(S source)  {
		try {
			return delegate.get(source);
		} catch(Exception e) {
			errorHandler.errorMappingField(key, source, null, e);
			return null;
		}
	}

    @Override
    public String toString() {
        return "FieldErrorHandlerMapper{delegate=" + delegate + '}';
	}


	public  static <S, T, K extends FieldKey<K>> Getter<S, T> of(K key, Getter<? super S, ? extends T> delegate,
																	  FieldMapperErrorHandler<? super K> errorHandler) {

		if (delegate instanceof BooleanGetter) {
			return new BooleanFieldErrorHandlerGetter<S, T, K>(key, delegate, errorHandler);
		}
		if (delegate instanceof ByteGetter) {
			return new ByteFieldErrorHandlerGetter<S, T, K>(key, delegate, errorHandler);
		}
		if (delegate instanceof CharacterGetter) {
			return new CharFieldErrorHandlerGetter<S, T, K>(key, delegate, errorHandler);
		}
		if (delegate instanceof ShortGetter) {
			return new ShortFieldErrorHandlerGetter<S, T, K>(key, delegate, errorHandler);
		}
		if (delegate instanceof IntGetter) {
			return new IntFieldErrorHandlerGetter<S, T, K>(key, delegate, errorHandler);
		}
		if (delegate instanceof LongGetter) {
			return new LongFieldErrorHandlerGetter<S, T, K>(key, delegate, errorHandler);
		}
		if (delegate instanceof FloatGetter) {
			return new FloatFieldErrorHandlerGetter<S, T, K>(key, delegate, errorHandler);
		}
		if (delegate instanceof DoubleGetter) {
			return new DoubleFieldErrorHandlerGetter<S, T, K>(key, delegate, errorHandler);
		}
		
		
		return new FieldErrorHandlerGetter<S, T, K>(key, delegate, errorHandler);
	}


	private static class BooleanFieldErrorHandlerGetter<S, T, K> extends FieldErrorHandlerGetter<S, T, K> implements BooleanGetter<T> {

		private BooleanGetter<T> pGetter;

		public BooleanFieldErrorHandlerGetter(K key, Getter<? super S, ? extends T> delegate, FieldMapperErrorHandler<? super K> errorHandler) {
			super(key, delegate, errorHandler);
			this.pGetter = (BooleanGetter<T>) delegate;
		}

		@Override
		public boolean getBoolean(T source) throws Exception {
			try {
				return pGetter.getBoolean(source);
			} catch(Exception e) {
				errorHandler.errorMappingField(key, source, null, e);
				return false;
			}
		}
	}
	
	private static class ByteFieldErrorHandlerGetter<S, T, K> extends FieldErrorHandlerGetter<S, T, K> implements ByteGetter<T> {

		private ByteGetter<T> pGetter;
		
		public ByteFieldErrorHandlerGetter(K key, Getter<? super S, ? extends T> delegate, FieldMapperErrorHandler<? super K> errorHandler) {
			super(key, delegate, errorHandler);
			this.pGetter = (ByteGetter<T>) delegate;
		}

		@Override
		public byte getByte(T source) throws Exception {
			try {
				return pGetter.getByte(source);
			} catch(Exception e) {
				errorHandler.errorMappingField(key, source, null, e);
				return 0;
			}
		}
	}

	private static class CharFieldErrorHandlerGetter<S, T, K> extends FieldErrorHandlerGetter<S, T, K> implements CharacterGetter<T> {

		private CharacterGetter<T> pGetter;

		public CharFieldErrorHandlerGetter(K key, Getter<? super S, ? extends T> delegate, FieldMapperErrorHandler<? super K> errorHandler) {
			super(key, delegate, errorHandler);
			this.pGetter = (CharacterGetter<T>) delegate;
		}

		@Override
		public char getCharacter(T source) throws Exception {
			try {
				return pGetter.getCharacter(source);
			} catch(Exception e) {
				errorHandler.errorMappingField(key, source, null, e);
				return 0;
			}
		}
	}

	private static class ShortFieldErrorHandlerGetter<S, T, K> extends FieldErrorHandlerGetter<S, T, K> implements ShortGetter<T> {

		private ShortGetter<T> pGetter;

		public ShortFieldErrorHandlerGetter(K key, Getter<? super S, ? extends T> delegate, FieldMapperErrorHandler<? super K> errorHandler) {
			super(key, delegate, errorHandler);
			this.pGetter = (ShortGetter<T>) delegate;
		}

		@Override
		public short getShort(T source) throws Exception {
			try {
				return pGetter.getShort(source);
			} catch(Exception e) {
				errorHandler.errorMappingField(key, source, null, e);
				return 0;
			}
		}
	}

	private static class IntFieldErrorHandlerGetter<S, T, K> extends FieldErrorHandlerGetter<S, T, K> implements IntGetter<T> {

		private IntGetter<T> pGetter;

		public IntFieldErrorHandlerGetter(K key, Getter<? super S, ? extends T> delegate, FieldMapperErrorHandler<? super K> errorHandler) {
			super(key, delegate, errorHandler);
			this.pGetter = (IntGetter<T>) delegate;
		}

		@Override
		public int getInt(T source) throws Exception {
			try {
				return pGetter.getInt(source);
			} catch(Exception e) {
				errorHandler.errorMappingField(key, source, null, e);
				return 0;
			}
		}
	}

	private static class LongFieldErrorHandlerGetter<S, T, K> extends FieldErrorHandlerGetter<S, T, K> implements LongGetter<T> {

		private LongGetter<T> pGetter;

		public LongFieldErrorHandlerGetter(K key, Getter<? super S, ? extends T> delegate, FieldMapperErrorHandler<? super K> errorHandler) {
			super(key, delegate, errorHandler);
			this.pGetter = (LongGetter<T>) delegate;
		}

		@Override
		public long getLong(T source) throws Exception {
			try {
				return pGetter.getLong(source);
			} catch(Exception e) {
				errorHandler.errorMappingField(key, source, null, e);
				return 0;
			}
		}
	}

	private static class FloatFieldErrorHandlerGetter<S, T, K> extends FieldErrorHandlerGetter<S, T, K> implements FloatGetter<T> {

		private FloatGetter<T> pGetter;

		public FloatFieldErrorHandlerGetter(K key, Getter<? super S, ? extends T> delegate, FieldMapperErrorHandler<? super K> errorHandler) {
			super(key, delegate, errorHandler);
			this.pGetter = (FloatGetter<T>) delegate;
		}

		@Override
		public float getFloat(T source) throws Exception {
			try {
				return pGetter.getFloat(source);
			} catch(Exception e) {
				errorHandler.errorMappingField(key, source, null, e);
				return 0;
			}
		}
	}

	private static class DoubleFieldErrorHandlerGetter<S, T, K> extends FieldErrorHandlerGetter<S, T, K> implements DoubleGetter<T> {

		private DoubleGetter<T> pGetter;

		public DoubleFieldErrorHandlerGetter(K key, Getter<? super S, ? extends T> delegate, FieldMapperErrorHandler<? super K> errorHandler) {
			super(key, delegate, errorHandler);
			this.pGetter = (DoubleGetter<T>) delegate;
		}

		@Override
		public double getDouble(T source) throws Exception {
			try {
				return pGetter.getDouble(source);
			} catch(Exception e) {
				errorHandler.errorMappingField(key, source, null, e);
				return 0;
			}
		}
	}
}
