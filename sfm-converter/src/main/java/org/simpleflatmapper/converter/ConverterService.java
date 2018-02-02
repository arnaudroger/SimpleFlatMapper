package org.simpleflatmapper.converter;

import org.simpleflatmapper.converter.impl.JavaBaseConverterFactoryProducer;
import org.simpleflatmapper.converter.impl.IdentityConverter;
//IFJAVA8_START
import org.simpleflatmapper.converter.impl.time.JavaTimeConverterFactoryProducer;
//IFJAVA8_END
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.ProducerServiceLoader;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConverterService {


    private static final ConverterService INSTANCE = new ConverterService(getConverterFactories());

    private static List<ConverterFactory> getConverterFactories() {
        final List<ConverterFactory> converterFactories = new ArrayList<ConverterFactory>();

        Consumer<ConverterFactory<?, ?>> factoryConsumer = new Consumer<ConverterFactory<?, ?>>() {
            @Override
            public void accept(ConverterFactory<?, ?> converterFactory) {
                converterFactories.add(converterFactory);
            }
        };

        new JavaBaseConverterFactoryProducer().produce(factoryConsumer);

        //IFJAVA8_START
        new JavaTimeConverterFactoryProducer().produce(factoryConsumer);
        //IFJAVA8_END

        ProducerServiceLoader.produceFromServiceLoader(ConverterFactoryProducer.class, factoryConsumer);

        return converterFactories;
    }


    public static ConverterService getInstance() {
        return INSTANCE;
    }

    private ConverterService(List<ConverterFactory> converters) {
        this.converters = converters;
    }

    public <F, P> Converter<? super F, ? extends P> findConverter(Class<F> inType, Class<P> outType, Object... params) {
        return findConverter((Type)inType, (Type)outType, params);
    }

    @SuppressWarnings("unchecked")
    public <F, P> Converter<? super F, ? extends P> findConverter(Type inType, Type outType, Object... params) {
        List<ScoredConverterFactory> potentials = new ArrayList<ScoredConverterFactory>();

        List<ScoredConverterFactory> tails = new ArrayList<ScoredConverterFactory>();

        if (TypeHelper.isAssignable(outType, inType)) {
            return new IdentityConverter();
        }
        ConvertingTypes targetedTypes = new ConvertingTypes(inType, outType);

        for(ConverterFactory converterFactory : converters) {
            ConvertingScore score = converterFactory.score(targetedTypes);
            int globalScore = score.getScore();
            if (globalScore >= 0) {
                potentials.add(new ScoredConverterFactory(globalScore, converterFactory));
            } else {
                int tailScore = score.getToScore();
                if (tailScore >= 0) {
                    tails.add(new ScoredConverterFactory(tailScore, converterFactory));
                }
            }
        }


        if (potentials.size() > 0) {
            Collections.sort(potentials);
            
            for(ScoredConverterFactory p : potentials) {
                Converter<F, P> converter = p.converterFactory.newConverter(targetedTypes, params);
                if (converter != null) return converter;
            }
            return null;
        } else {
            if (tails.size() > 0) {
                Collections.sort(tails);

                ComposedConverter composedConverter = null;
                int currentScore = 0;
                
                for(ScoredConverterFactory sfactory : tails) {
                    // break when score not to the max and have a converter
                    if (composedConverter != null && currentScore > sfactory.score) {
                        return composedConverter;
                    }
                    Type tailFactoryInType = sfactory.converterFactory.getFromType();
                    if (outType != tailFactoryInType) { // ignore when no progress
                        Converter headConverter = (Converter<? super F, ? extends P>) findConverter(inType, tailFactoryInType, params);
                        if (headConverter != null) {
                            Converter tailConverter = sfactory.converterFactory.newConverter(new ConvertingTypes(tailFactoryInType, targetedTypes.getTo()), params);
                            if (tailConverter != null) {
                                ComposedConverter c = new ComposedConverter(headConverter, tailConverter);
                                // override converter only if depth is less\
                                if (composedConverter == null || c.depth() < composedConverter.depth()) {
                                    composedConverter = c;
                                }
                            }
                        }
                    }
                }
                return composedConverter;
            }
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
