package org.simpleflatmapper.converter.impl;


import org.simpleflatmapper.converter.AbstractConverterFactory;
import org.simpleflatmapper.converter.AbstractConverterFactoryProducer;
import org.simpleflatmapper.converter.ContextFactoryBuilder;
import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.converter.ConverterFactory;
import org.simpleflatmapper.converter.ConvertingScore;
import org.simpleflatmapper.converter.ConvertingTypes;

import org.simpleflatmapper.converter.ToStringConverter;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.Supplier;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.util.date.DateFormatSupplier;
import org.simpleflatmapper.util.date.DefaultDateFormatSupplier;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
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
		
		factoryConverter(consumer, new DateConverterFactory());

		factoryConverter (consumer, new EnumConverterFactory());

		factoryConverter (consumer, new NumberEnumConverterFactory());

		factoryConverter (consumer, new ObjectEnumConverterFactory());

		constantConverter(consumer, Object.class, String.class, ToStringConverter.INSTANCE);
		constantConverter(consumer, Object.class, URL.class, new ToStringToURLConverter());
	}

	private static class DateConverterFactory extends AbstractConverterFactory<CharSequence, Date> {
		public DateConverterFactory() {
			super(CharSequence.class, Date.class);
		}

		@Override
		public Converter<? super CharSequence, ? extends Date> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
			List<String> formats = getFormat(params);
			TimeZone timeZone = getTimeZone(params);
			if (formats.isEmpty()) {
				return null;
			} else if (formats.size() == 1) {
				final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formats.get(0));
				simpleDateFormat.setTimeZone(timeZone);
				int contextIndex = contextFactoryBuilder.addSupplier(new Supplier<SimpleDateFormat>() {
					@Override
					public SimpleDateFormat get() {
						return (SimpleDateFormat) simpleDateFormat.clone();
					}
				});
				return new CharSequenceToDateConverter(contextIndex);
			} else {
				final SimpleDateFormat[] simpleDateFormats = new SimpleDateFormat[formats.size()];
				for(int i = 0; i < simpleDateFormats.length; i++) {
					final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formats.get(simpleDateFormats.length - i - 1));
					simpleDateFormat.setTimeZone(timeZone);
					simpleDateFormats[i] = simpleDateFormat;
				}
				
				int contextIndex = contextFactoryBuilder.addSupplier(new Supplier<SimpleDateFormat[]>() {
					@Override
					public SimpleDateFormat[] get() {
						SimpleDateFormat[] simpleDateFormatsCopy = new SimpleDateFormat[simpleDateFormats.length];
						for(int i = 0; i < simpleDateFormats.length; i++) {
							simpleDateFormatsCopy[i] = (SimpleDateFormat) simpleDateFormats[i].clone();
						}
						return simpleDateFormatsCopy;
					}
				});
				
				return new MultiFormatCharSequenceToDateConverter(contextIndex);
			}
		}

		private TimeZone getTimeZone(Object[] params) {
			TimeZone defaultValue = TimeZone.getDefault();
			if (params != null) {
				for(Object o : params) {
					if (o instanceof Supplier) {
						Supplier s = (Supplier) o;
						Object o1 = s.get();
						if (o1 instanceof TimeZone) {
							return (TimeZone) o1;
						}
					}
				}

			}
			return defaultValue;
		}

		private List<String> getFormat(Object[] params) {
			
			String defaultValue = null;
			List<String> formats = new ArrayList<String>();
			if (params != null) {
				for(Object o : params) {
					if (o instanceof DefaultDateFormatSupplier) {
						defaultValue = ((DefaultDateFormatSupplier) o).get();
					} else if (o instanceof DateFormatSupplier) {
						formats.add(((DateFormatSupplier) o).get());
					}
				}
				
			}
			if (formats.isEmpty() && defaultValue != null) {
				formats.add(defaultValue);
			}
			return formats;
		}
	}

	private static class EnumConverterFactory extends AbstractConverterFactory<CharSequence, Enum> {
		public EnumConverterFactory() {
			super(CharSequence.class, Enum.class);
		}

		@SuppressWarnings("unchecked")
		public Converter<? super CharSequence, ? extends Enum> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
			return new CharSequenceToEnumConverter(TypeHelper.toClass(targetedTypes.getTo()));
		}

		@Override
		public ConvertingScore score(ConvertingTypes targetedTypes) {
			if (TypeHelper.isAssignable(Enum.class, targetedTypes.getTo())) {
				return new ConvertingScore(ConvertingTypes.getSourceScore(convertingTypes.getFrom(), targetedTypes.getFrom()), ConvertingScore.MAX_SCORE);
			}
			return ConvertingScore.NO_MATCH;
		}
	}

	private static class NumberEnumConverterFactory extends AbstractConverterFactory<Number, Enum> {
		public NumberEnumConverterFactory() {
			super(Number.class, Enum.class);
		}

		@SuppressWarnings("unchecked")
		public Converter<? super Number, ? extends Enum> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
			return new NumberToEnumConverter(TypeHelper.toClass(targetedTypes.getTo()));
		}

		@Override
		public ConvertingScore score(ConvertingTypes targetedTypes) {
			if (TypeHelper.isAssignable(Enum.class, targetedTypes.getTo())) {
				return new ConvertingScore(ConvertingTypes.getSourceScore(convertingTypes.getFrom(), targetedTypes.getFrom()), ConvertingScore.MAX_SCORE);
			}
			return ConvertingScore.NO_MATCH;
		}
	}

	private static class ObjectEnumConverterFactory extends AbstractConverterFactory<Object, Enum> {
		public ObjectEnumConverterFactory() {
			super(Object.class, Enum.class);
		}

		@SuppressWarnings("unchecked")
		public Converter<? super Object, ? extends Enum> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
			return new ObjectToEnumConverter(TypeHelper.toClass(targetedTypes.getTo()));
		}

		@Override
		public ConvertingScore score(ConvertingTypes targetedTypes) {
			if (TypeHelper.isAssignable(Enum.class, targetedTypes.getTo())) {
				return new ConvertingScore(ConvertingTypes.getSourceScore(convertingTypes.getFrom(), targetedTypes.getFrom()), ConvertingScore.MAX_SCORE);
			}
			return ConvertingScore.NO_MATCH;
		}
	}
}
