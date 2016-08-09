package org.simpleflatmapper.converter;

import org.simpleflatmapper.converter.impl.JavaBaseConverterFactoryProducer;
import org.simpleflatmapper.converter.impl.IdentityConverter;
//IFJAVA8_START
import org.simpleflatmapper.converter.impl.time.JavaTimeConverterFactoryProducer;
//IFJAVA8_END
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

public class ConverterService {


    private static final ConverterService INSTANCE = new ConverterService(getConverterFactories());

    private static List<ConverterFactory> getConverterFactories() {
        final List<ConverterFactory> converterFactories = new ArrayList<ConverterFactory>();

        Consumer<ConverterFactory> factoryConsumer = new Consumer<ConverterFactory>() {
            @Override
            public void accept(ConverterFactory converterFactory) {
                converterFactories.add(converterFactory);
            }
        };

        new JavaBaseConverterFactoryProducer().produce(factoryConsumer);

        //IFJAVA8_START
        new JavaTimeConverterFactoryProducer().produce(factoryConsumer);
        //IFJAVA8_END

        ServiceLoader<ConverterFactoryProducer> serviceLoader = ServiceLoader.load(ConverterFactoryProducer.class);

        Iterator<ConverterFactoryProducer> iterator = serviceLoader.iterator();

        while(iterator.hasNext()) {
            try {
                iterator.next().produce(factoryConsumer);
            } catch (ServiceConfigurationError e) {
                System.err.println("Unexpected error on listing ConverterFactoryProducer, prop classloader visibility " + e);
            }
        }

        return converterFactories;
    }


    public static ConverterService getInstance() {
        return INSTANCE;
    }

    private ConverterService(List<ConverterFactory> converters) {
        this.converters = converters;
    }

    public <P, F> Converter<? super F, ? extends P> findConverter(Class<F> inType, Class<P> outType, Object... params) {
        return findConverter((Type)inType, (Type)outType, params);
    }

    @SuppressWarnings("unchecked")
    public <P, F> Converter<? super F, ? extends P> findConverter(Type inType, Type outType, Object... params) {
        List<ScoredConverterFactory> potentials = new ArrayList<ScoredConverterFactory>();

        if (TypeHelper.areEquals(inType, outType)) {
            return new IdentityConverter();
        }
        ConvertingTypes targetedTypes = new ConvertingTypes(inType, outType);

        for(ConverterFactory converterFactory : converters) {
            int score = converterFactory.score(targetedTypes);
            if (score >= 0) {
                potentials.add(new ScoredConverterFactory(score, converterFactory));
            }
        }

        Collections.sort(potentials);

        if (potentials.size() > 0) {
            return (Converter<F, P>) potentials.get(0).converterFactory.newConverter(targetedTypes, params);
        } else {
            return null;
        }
    }

    private final List<ConverterFactory> converters;


    private static class ScoredConverterFactory implements Comparable<ScoredConverterFactory>{
        private final int score;
        private final ConverterFactory converterFactory;

        private ScoredConverterFactory(int score, ConverterFactory converterFactory) {
            this.score = score;
            this.converterFactory = converterFactory;
        }

        @Override
        public int compareTo(ScoredConverterFactory o) {
            return o.score - score;
        }
    }

}
