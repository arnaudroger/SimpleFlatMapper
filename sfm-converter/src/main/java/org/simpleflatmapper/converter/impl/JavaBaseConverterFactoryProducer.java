package org.simpleflatmapper.converter.impl;


import org.simpleflatmapper.converter.AbstractConverterFactory;
import org.simpleflatmapper.converter.AbstractConverterFactoryProducer;
import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.converter.ConverterFactory;
import org.simpleflatmapper.converter.ConvertingScore;
import org.simpleflatmapper.converter.ConvertingTypes;

import org.simpleflatmapper.converter.ToStringConverter;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.util.date.DateFormatSupplier;
import org.simpleflatmapper.util.date.DefaultDateFormatSupplier;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

public class JavaBaseConverterFactoryProducer extends AbstractConverterFactoryProducer {
	@Override
	public void produce(Consumer<? super ConverterFactory<?, ?>> consumer) {
		constantConverter(consumer, Number.class, Byte.class      , new NumberByteConverter());
		constantConverter(consumer, Number.class, Short.class     , new NumberShortConverter());
		constantConverter(consumer, Number.class, Integer.class   , new NumberIntegerConverter());
		constantConverter(consumer, Number.class, Long.class      , new NumberLongConverter());
		constantConverter(consumer, Number.class, Float.class     , new NumberFloatConverter());
		constantConverter(consumer, Number.class, Double.class    , new NumberDoubleConverter());
		constantConverter(consumer, Number.class, BigDecimal.class, new NumberBigDecimalConverter());
		constantConverter(consumer, Number.class, BigInteger.class, new NumberBigIntegerConverter());


		constantConverter(consumer, CharSequence.class, Byte.class,       new CharSequenceByteConverter());
		constantConverter(consumer, CharSequence.class, BigInteger.class, new CharSequenceBigInteger());
		constantConverter(consumer, CharSequence.class, BigDecimal.class, new CharSequenceBigDecimal());
		constantConverter(consumer, CharSequence.class, Character.class,  new CharSequenceCharacterConverter());
		constantConverter(consumer, CharSequence.class, Short.class,      new CharSequenceShortConverter());
		constantConverter(consumer, CharSequence.class, Integer.class,    new CharSequenceIntegerConverter());
		constantConverter(consumer, CharSequence.class, Long.class,       new CharSequenceLongConverter());
		constantConverter(consumer, CharSequence.class, Float.class,      new CharSequenceFloatConverter());
		constantConverter(consumer, CharSequence.class, Double.class,     new CharSequenceDoubleConverter());
		constantConverter(consumer, CharSequence.class, UUID.class,       new CharSequenceUUIDConverter());
		constantConverter(consumer, CharSequence.class, Boolean.class,    new CharSequenceBooleanConverter());
		
		factoryConverter(consumer, new AbstractConverterFactory<CharSequence, Date>(CharSequence.class, Date.class) {
			@Override
			public Converter<? super CharSequence, ? extends Date> newConverter(ConvertingTypes targetedTypes, Object... params) {
				String format = getFormat(params);
				if (format != null) {
					return new CharSequenceToDateConverter(format);
				} else return null;
			}

			private String getFormat(Object[] params) {
				String defaultValue = null;
				if (params != null) {
					for(Object o : params) {
						if (o instanceof DefaultDateFormatSupplier) {
							defaultValue = ((DefaultDateFormatSupplier) o).get();
						} else if (o instanceof DateFormatSupplier) {
							return ((DateFormatSupplier) o).get();
						}
					}
					
				}
				return defaultValue;
			}
		});

		factoryConverter (consumer, new AbstractConverterFactory<CharSequence, Enum>(CharSequence.class, Enum.class) {
			@SuppressWarnings("unchecked")
			public Converter<? super CharSequence, ? extends Enum> newConverter(ConvertingTypes targetedTypes, Object... params) {
				return new CharSequenceToEnumConverter(TypeHelper.toClass(targetedTypes.getTo()));
			}

			@Override
			public ConvertingScore score(ConvertingTypes targetedTypes) {
				if (TypeHelper.isAssignable(Enum.class, targetedTypes.getTo())) {
					return new ConvertingScore(ConvertingTypes.getSourceScore(convertingTypes.getFrom(), targetedTypes.getFrom()), ConvertingScore.MAX_SCORE);
				}
				return ConvertingScore.NO_MATCH;
			}
		});

		factoryConverter (consumer, new AbstractConverterFactory<Number, Enum>(Number.class, Enum.class) {
			@SuppressWarnings("unchecked")
			public Converter<? super Number, ? extends Enum> newConverter(ConvertingTypes targetedTypes, Object... params) {
				return new NumberToEnumConverter(TypeHelper.toClass(targetedTypes.getTo()));
			}

			@Override
			public ConvertingScore score(ConvertingTypes targetedTypes) {
				if (TypeHelper.isAssignable(Enum.class, targetedTypes.getTo())) {
					return new ConvertingScore(ConvertingTypes.getSourceScore(convertingTypes.getFrom(), targetedTypes.getFrom()), ConvertingScore.MAX_SCORE);
				}
				return ConvertingScore.NO_MATCH;
			}
		});

		factoryConverter (consumer, new AbstractConverterFactory<Object, Enum>(Object.class, Enum.class) {
			@SuppressWarnings("unchecked")
			public Converter<? super Object, ? extends Enum> newConverter(ConvertingTypes targetedTypes, Object... params) {
				return new ObjectToEnumConverter(TypeHelper.toClass(targetedTypes.getTo()));
			}

			@Override
			public ConvertingScore score(ConvertingTypes targetedTypes) {
				if (TypeHelper.isAssignable(Enum.class, targetedTypes.getTo())) {
					return new ConvertingScore(ConvertingTypes.getSourceScore(convertingTypes.getFrom(), targetedTypes.getFrom()), ConvertingScore.MAX_SCORE);
				}
				return ConvertingScore.NO_MATCH;
			}
		});

		constantConverter(consumer, Object.class, String.class, ToStringConverter.INSTANCE);
		constantConverter(consumer, Object.class, URL.class, new ToStringToURLConverter());
	}
}
