package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapperErrorHandler;
import org.simpleflatmapper.map.getter.BooleanContextualGetter;
import org.simpleflatmapper.map.getter.ByteContextualGetter;
import org.simpleflatmapper.map.getter.CharacterContextualGetter;
import org.simpleflatmapper.map.getter.DoubleContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.FloatContextualGetter;
import org.simpleflatmapper.map.getter.IntContextualGetter;
import org.simpleflatmapper.map.getter.LongContextualGetter;
import org.simpleflatmapper.map.getter.ShortContextualGetter;


public class FieldErrorHandlerGetter<S, T, K> implements ContextualGetter<S, T> {

	public final ContextualGetter<? super S, ? extends T> delegate;
	public final FieldMapperErrorHandler<? super K> errorHandler;
	public final K key;
	
	public FieldErrorHandlerGetter(K key, ContextualGetter<? super S, ? extends T> delegate,
                                   FieldMapperErrorHandler<? super K> errorHandler) {
		super();
		if (key == null) throw new IllegalArgumentException("key is null");
		if (delegate == null) throw new IllegalArgumentException("delegate is null");
		if (errorHandler == null) throw new IllegalArgumentException("errorHandler is null");
		this.key = key;
		this.delegate = delegate;
		this.errorHandler = errorHandler;
	}

	@Override
	public T get(S source, Context context)  {
		try {
			return delegate.get(source, context);
		} catch(Exception e) {
			errorHandler.errorMappingField(key, source, null, e, context);
			return null;
		}
	}

    @Override
    public String toString() {
        return "FieldErrorHandlerMapper{delegate=" + delegate + '}';
	}


	public  static <S, T, K extends FieldKey<K>> ContextualGetter<S, T> of(K key, ContextualGetter<? super S, ? extends T> delegate,
																		   FieldMapperErrorHandler<? super K> errorHandler) {

		if (delegate instanceof BooleanContextualGetter) {
			return new BooleanFieldErrorHandlerGetter<S, T, K>(key, delegate, errorHandler);
		}
		if (delegate instanceof ByteContextualGetter) {
			return new ByteFieldErrorHandlerGetter<S, T, K>(key, delegate, errorHandler);
		}
		if (delegate instanceof CharacterContextualGetter) {
			return new CharFieldErrorHandlerGetter<S, T, K>(key, delegate, errorHandler);
		}
		if (delegate instanceof ShortContextualGetter) {
			return new ShortFieldErrorHandlerGetter<S, T, K>(key, delegate, errorHandler);
		}
		if (delegate instanceof IntContextualGetter) {
			return new IntFieldErrorHandlerGetter<S, T, K>(key, delegate, errorHandler);
		}
		if (delegate instanceof LongContextualGetter) {
			return new LongFieldErrorHandlerGetter<S, T, K>(key, delegate, errorHandler);
		}
		if (delegate instanceof FloatContextualGetter) {
			return new FloatFieldErrorHandlerGetter<S, T, K>(key, delegate, errorHandler);
		}
		if (delegate instanceof DoubleContextualGetter) {
			return new DoubleFieldErrorHandlerGetter<S, T, K>(key, delegate, errorHandler);
		}
		
		
		return new FieldErrorHandlerGetter<S, T, K>(key, delegate, errorHandler);
	}


	private static class BooleanFieldErrorHandlerGetter<S, T, K> extends FieldErrorHandlerGetter<S, T, K> implements BooleanContextualGetter<T> {

		private BooleanContextualGetter<T> pGetter;

		public BooleanFieldErrorHandlerGetter(K key, ContextualGetter<? super S, ? extends T> delegate, FieldMapperErrorHandler<? super K> errorHandler) {
			super(key, delegate, errorHandler);
			this.pGetter = (BooleanContextualGetter<T>) delegate;
		}

		@Override
		public boolean getBoolean(T source, Context mappingContext) throws Exception {
			try {
				return pGetter.getBoolean(source, mappingContext);
			} catch(Exception e) {
				errorHandler.errorMappingField(key, source, null, e,  mappingContext);
				return false;
			}
		}
	}
	
	private static class ByteFieldErrorHandlerGetter<S, T, K> extends FieldErrorHandlerGetter<S, T, K> implements ByteContextualGetter<T> {

		private ByteContextualGetter<T> pGetter;
		
		public ByteFieldErrorHandlerGetter(K key, ContextualGetter<? super S, ? extends T> delegate, FieldMapperErrorHandler<? super K> errorHandler) {
			super(key, delegate, errorHandler);
			this.pGetter = (ByteContextualGetter<T>) delegate;
		}

		@Override
		public byte getByte(T source, Context mappingContext) throws Exception {
			try {
				return pGetter.getByte(source, mappingContext);
			} catch(Exception e) {
				errorHandler.errorMappingField(key, source, null, e, mappingContext);
				return 0;
			}
		}
	}

	private static class CharFieldErrorHandlerGetter<S, T, K> extends FieldErrorHandlerGetter<S, T, K> implements CharacterContextualGetter<T> {

		private CharacterContextualGetter<T> pGetter;

		public CharFieldErrorHandlerGetter(K key, ContextualGetter<? super S, ? extends T> delegate, FieldMapperErrorHandler<? super K> errorHandler) {
			super(key, delegate, errorHandler);
			this.pGetter = (CharacterContextualGetter<T>) delegate;
		}

		@Override
		public char getCharacter(T source, Context mappingContext) throws Exception {
			try {
				return pGetter.getCharacter(source, mappingContext);
			} catch(Exception e) {
				errorHandler.errorMappingField(key, source, null, e, mappingContext);
				return 0;
			}
		}
	}

	private static class ShortFieldErrorHandlerGetter<S, T, K> extends FieldErrorHandlerGetter<S, T, K> implements ShortContextualGetter<T> {

		private ShortContextualGetter<T> pGetter;

		public ShortFieldErrorHandlerGetter(K key, ContextualGetter<? super S, ? extends T> delegate, FieldMapperErrorHandler<? super K> errorHandler) {
			super(key, delegate, errorHandler);
			this.pGetter = (ShortContextualGetter<T>) delegate;
		}

		@Override
		public short getShort(T source, Context mappingContext) throws Exception {
			try {
				return pGetter.getShort(source, mappingContext);
			} catch(Exception e) {
				errorHandler.errorMappingField(key, source, null, e, mappingContext);
				return 0;
			}
		}
	}

	private static class IntFieldErrorHandlerGetter<S, T, K> extends FieldErrorHandlerGetter<S, T, K> implements IntContextualGetter<T> {

		private IntContextualGetter<T> pGetter;

		public IntFieldErrorHandlerGetter(K key, ContextualGetter<? super S, ? extends T> delegate, FieldMapperErrorHandler<? super K> errorHandler) {
			super(key, delegate, errorHandler);
			this.pGetter = (IntContextualGetter<T>) delegate;
		}

		@Override
		public int getInt(T source, Context mappingContext) throws Exception {
			try {
				return pGetter.getInt(source, mappingContext);
			} catch(Exception e) {
				errorHandler.errorMappingField(key, source, null, e, mappingContext);
				return 0;
			}
		}
	}

	private static class LongFieldErrorHandlerGetter<S, T, K> extends FieldErrorHandlerGetter<S, T, K> implements LongContextualGetter<T> {

		private LongContextualGetter<T> pGetter;

		public LongFieldErrorHandlerGetter(K key, ContextualGetter<? super S, ? extends T> delegate, FieldMapperErrorHandler<? super K> errorHandler) {
			super(key, delegate, errorHandler);
			this.pGetter = (LongContextualGetter<T>) delegate;
		}

		@Override
		public long getLong(T source, Context mappingContext) throws Exception {
			try {
				return pGetter.getLong(source, mappingContext);
			} catch(Exception e) {
				errorHandler.errorMappingField(key, source, null, e, mappingContext);
				return 0;
			}
		}
	}

	private static class FloatFieldErrorHandlerGetter<S, T, K> extends FieldErrorHandlerGetter<S, T, K> implements FloatContextualGetter<T> {

		private FloatContextualGetter<T> pGetter;

		public FloatFieldErrorHandlerGetter(K key, ContextualGetter<? super S, ? extends T> delegate, FieldMapperErrorHandler<? super K> errorHandler) {
			super(key, delegate, errorHandler);
			this.pGetter = (FloatContextualGetter<T>) delegate;
		}

		@Override
		public float getFloat(T source, Context mappingContext) throws Exception {
			try {
				return pGetter.getFloat(source, mappingContext);
			} catch(Exception e) {
				errorHandler.errorMappingField(key, source, null, e, mappingContext);
				return 0;
			}
		}
	}

	private static class DoubleFieldErrorHandlerGetter<S, T, K> extends FieldErrorHandlerGetter<S, T, K> implements DoubleContextualGetter<T> {

		private DoubleContextualGetter<T> pGetter;

		public DoubleFieldErrorHandlerGetter(K key, ContextualGetter<? super S, ? extends T> delegate, FieldMapperErrorHandler<? super K> errorHandler) {
			super(key, delegate, errorHandler);
			this.pGetter = (DoubleContextualGetter<T>) delegate;
		}

		@Override
		public double getDouble(T source, Context mappingContext) throws Exception {
			try {
				return pGetter.getDouble(source, mappingContext);
			} catch(Exception e) {
				errorHandler.errorMappingField(key, source, null, e, mappingContext);
				return 0;
			}
		}
	}
}
