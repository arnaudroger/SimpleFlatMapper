package org.simpleflatmapper.converter.test;

import org.simpleflatmapper.converter.*;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.TypeHelper;

public class FooConverterFactoryProducer extends AbstractContextualConverterFactoryProducer {
    @Override
    public void produce(Consumer<? super ContextualConverterFactory<?, ?>> consumer) {
        constantConverter(consumer, Bar.class, Foo.class, new ContextualConverter<Bar, Foo>() {
            @Override
            public Foo convert(Bar in, Context context) throws Exception {
                return new Foo(in);
            }

        });
        AbstractContextualConverterFactory<Number, IP> abstractContextualConverterFactory = new AbstractContextualConverterFactory<Number, IP>(Number.class, IP.class) {
            @Override
            public ContextualConverter<? super Number, ? extends IP> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder,  Object... params) {
                return new ContextualConverter<Number, IP>() {

                    @Override
                    public IP convert(Number in, Context context) throws Exception {
                        return MyEnum.ZERO;
                    }
                };
            }
            @Override
            public ConvertingScore score(ConvertingTypes targetedTypes) {
                if (TypeHelper.isAssignable(IP.class, targetedTypes.getTo())) {
                    return new ConvertingScore(ConvertingTypes.getSourceScore(getFromType(), targetedTypes.getFrom()), ConvertingScore.MAX_SCORE * 2);
                }
                return super.score(targetedTypes);
            }
        };
        this.<Number, IP>factoryConverter(consumer, abstractContextualConverterFactory);
    }
}
