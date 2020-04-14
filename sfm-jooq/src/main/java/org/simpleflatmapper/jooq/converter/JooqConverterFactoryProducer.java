package org.simpleflatmapper.jooq.converter;

import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.jooq.types.UShort;
import org.simpleflatmapper.converter.AbstractContextualConverterFactoryProducer;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;
import org.simpleflatmapper.converter.ContextualConverterFactory;
import org.simpleflatmapper.util.Consumer;

import java.math.BigInteger;

public class JooqConverterFactoryProducer extends AbstractContextualConverterFactoryProducer {
    @Override
    public void produce(Consumer<? super ContextualConverterFactory<?, ?>> consumer) {
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
