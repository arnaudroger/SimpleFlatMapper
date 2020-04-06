package org.simpleflatmapper.jooq.converter;

import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.jooq.types.UShort;
import org.simpleflatmapper.converter.AbstractContextualConverterFactory;
import org.simpleflatmapper.converter.AbstractContextualConverterFactoryProducer;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextFactoryBuilder;
import org.simpleflatmapper.converter.ContextualConverter;
import org.simpleflatmapper.converter.ContextualConverterFactory;
import org.simpleflatmapper.converter.ConvertingTypes;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.Supplier;
import org.simpleflatmapper.util.SupplierHelper;

//IFJAVA8_START
import org.simpleflatmapper.jdbc.converter.time.DateToLocalDateConverter;
import org.simpleflatmapper.jdbc.converter.time.TimeToLocalTimeConverter;
import org.simpleflatmapper.jdbc.converter.time.TimeToOffsetTimeConverter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
//IFJAVA8_END
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;

public class JooqConverterFactoryProducer extends AbstractContextualConverterFactoryProducer {
    @Override
    public void produce(Consumer<? super ContextualConverterFactory<?, ?>> consumer) {
        //IFJAVA8_START
        constantConverter(consumer, Time.class, LocalTime.class, new TimeToLocalTimeConverter());
        constantConverter(consumer, Date.class, LocalDate.class, new DateToLocalDateConverter());
        factoryConverter(consumer, new AbstractContextualConverterFactory<Time, OffsetTime>(Time.class, OffsetTime.class) {
            @Override
            public ContextualConverter<Time, OffsetTime> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                ZoneOffset zoneOffset = getZoneOffset(params);
                return new TimeToOffsetTimeConverter(zoneOffset);
            }

            @SuppressWarnings("unchecked")
            private ZoneOffset getZoneOffset(Object[] params) {
                for(Object prop : params) {
                    if (prop instanceof ZoneOffset) {
                        return (ZoneOffset) prop;
                    } else if (SupplierHelper.isSupplierOf(prop, ZoneOffset.class)) {
                        return ((Supplier<ZoneOffset>)prop).get();
                    }
                }

                return ZoneOffset.UTC;
            }
        });
        //IFJAVA8_END

        this.constantConverter(consumer, byte.class, UByte.class, new ContextualConverter<Byte, UByte>() {
            @Override
            public UByte convert(Byte in, Context context) throws Exception {
                if (in == null) {
                    return null;
                }
                return UByte.valueOf(in);
            }
        });
        this.constantConverter(consumer, short.class, UByte.class, new ContextualConverter<Short, UByte>() {
            @Override
            public UByte convert(Short in, Context context) throws Exception {
                if (in == null) {
                    return null;
                }
                return UByte.valueOf(in);
            }
        });
        this.constantConverter(consumer, int.class, UByte.class, new ContextualConverter<Integer, UByte>() {
            @Override
            public UByte convert(Integer in, Context context) throws Exception {
                if (in == null) {
                    return null;
                }
                return UByte.valueOf(in);
            }
        });
        this.constantConverter(consumer, long.class, UByte.class, new ContextualConverter<Long, UByte>() {
            @Override
            public UByte convert(Long in, Context context) throws Exception {
                if (in == null) {
                    return null;
                }
                return UByte.valueOf(in);
            }
        });

        this.constantConverter(consumer, short.class, UShort.class, new ContextualConverter<Short, UShort>() {
            @Override
            public UShort convert(Short in, Context context) throws Exception {
                if (in == null) {
                    return null;
                }
                return UShort.valueOf(in);
            }
        });
        this.constantConverter(consumer, int.class, UShort.class, new ContextualConverter<Integer, UShort>() {
            @Override
            public UShort convert(Integer in, Context context) throws Exception {
                if (in == null) {
                    return null;
                }
                return UShort.valueOf(in);
            }
        });

        this.constantConverter(consumer, int.class, UInteger.class, new ContextualConverter<Integer, UInteger>() {
            @Override
            public UInteger convert(Integer in, Context context) throws Exception {
                if (in == null) {
                    return null;
                }
                return UInteger.valueOf(in);
            }
        });
        this.constantConverter(consumer, long.class, UInteger.class, new ContextualConverter<Long, UInteger>() {
            @Override
            public UInteger convert(Long in, Context context) throws Exception {
                if (in == null) {
                    return null;
                }
                return UInteger.valueOf(in);
            }
        });

        this.constantConverter(consumer, long.class, ULong.class, new ContextualConverter<Long, ULong>() {
            @Override
            public ULong convert(Long in, Context context) throws Exception {
                if (in == null) {
                    return null;
                }
                return ULong.valueOf(in);
            }
        });
        this.constantConverter(consumer, BigInteger.class, ULong.class, new ContextualConverter<BigInteger, ULong>() {
            @Override
            public ULong convert(BigInteger in, Context context) throws Exception {
                if (in == null) {
                    return null;
                }
                return ULong.valueOf(in);
            }
        });
        this.constantConverter(consumer, String.class, JSONObject.class, new ContextualConverter<String, JSONObject>() {
            @Override
            public JSONObject convert(String in, Context context) throws Exception {
                if (in == null) {
                    return null;
                }
                JSONParser parser = new JSONParser();
                return (JSONObject)parser.parse(in);
            }
        });

    }
}
