package org.simpleflatmapper.jooq.converter;

import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.jooq.types.UShort;
import org.simpleflatmapper.converter.AbstractConverterFactoryProducer;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.converter.ConverterFactory;
import org.simpleflatmapper.util.Consumer;

import java.math.BigInteger;

public class JooqConverterFactoryProducer extends AbstractConverterFactoryProducer {
    @Override
    public void produce(Consumer<? super ConverterFactory<?, ?>> consumer) {
        this.constantConverter(consumer, byte.class, UByte.class, new Converter<Byte, UByte>() {
            @Override
            public UByte convert(Byte in, Context context) throws Exception {
                if (in == null) {
                    return null;
                }
                return UByte.valueOf(in);
            }
        });
        this.constantConverter(consumer, short.class, UByte.class, new Converter<Short, UByte>() {
            @Override
            public UByte convert(Short in, Context context) throws Exception {
                if (in == null) {
                    return null;
                }
                return UByte.valueOf(in);
            }
        });
        this.constantConverter(consumer, int.class, UByte.class, new Converter<Integer, UByte>() {
            @Override
            public UByte convert(Integer in, Context context) throws Exception {
                if (in == null) {
                    return null;
                }
                return UByte.valueOf(in);
            }
        });
        this.constantConverter(consumer, long.class, UByte.class, new Converter<Long, UByte>() {
            @Override
            public UByte convert(Long in, Context context) throws Exception {
                if (in == null) {
                    return null;
                }
                return UByte.valueOf(in);
            }
        });

        this.constantConverter(consumer, short.class, UShort.class, new Converter<Short, UShort>() {
            @Override
            public UShort convert(Short in, Context context) throws Exception {
                if (in == null) {
                    return null;
                }
                return UShort.valueOf(in);
            }
        });
        this.constantConverter(consumer, int.class, UShort.class, new Converter<Integer, UShort>() {
            @Override
            public UShort convert(Integer in, Context context) throws Exception {
                if (in == null) {
                    return null;
                }
                return UShort.valueOf(in);
            }
        });

        this.constantConverter(consumer, int.class, UInteger.class, new Converter<Integer, UInteger>() {
            @Override
            public UInteger convert(Integer in, Context context) throws Exception {
                if (in == null) {
                    return null;
                }
                return UInteger.valueOf(in);
            }
        });
        this.constantConverter(consumer, long.class, UInteger.class, new Converter<Long, UInteger>() {
            @Override
            public UInteger convert(Long in, Context context) throws Exception {
                if (in == null) {
                    return null;
                }
                return UInteger.valueOf(in);
            }
        });

        this.constantConverter(consumer, long.class, ULong.class, new Converter<Long, ULong>() {
            @Override
            public ULong convert(Long in, Context context) throws Exception {
                if (in == null) {
                    return null;
                }
                return ULong.valueOf(in);
            }
        });
        this.constantConverter(consumer, BigInteger.class, ULong.class, new Converter<BigInteger, ULong>() {
            @Override
            public ULong convert(BigInteger in, Context context) throws Exception {
                if (in == null) {
                    return null;
                }
                return ULong.valueOf(in);
            }
        });
        this.constantConverter(consumer, String.class, JSONObject.class, new Converter<String, JSONObject>() {
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
