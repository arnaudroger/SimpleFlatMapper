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

    public <F, P> Converter<? super F, ? extends P> findConverter(Class<F> inType, Class<P> outType, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
        return findConverter((Type)inType, (Type)outType, contextFactoryBuilder, params);
    }

    @SuppressWarnings("unchecked")
    public <F, P> Converter<? super F, ? extends P> findConverter(Type inType, Type outType, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
        if (TypeHelper.isAssignable(outType, inType)) {
            return new IdentityConverter();
        }

        List<ScoredConverterFactory> potentials = findConverterFactories(inType, outType, params);


        ConvertingTypes targetedTypes = new ConvertingTypes(inType, outType);
        for(ScoredConverterFactory p : potentials) {
            Converter<F, P> converter = p.converterFactory.newConverter(targetedTypes, contextFactoryBuilder, params);
            if (converter != null) return converter;
        }
        return null;

    }

    @SuppressWarnings("unchecked")
    public  List<ScoredConverterFactory> findConverterFactories(Type inType, Type outType, Object... params) {
        List<ScoredConverterFactory> potentials = new ArrayList<ScoredConverterFactory>();

        List<ScoredConverterFactory> tails = new ArrayList<ScoredConverterFactory>();

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


        if (potentials.isEmpty()) {
            if (tails.size() > 0) {
                Collections.sort(tails);
                int currentScore = Integer.MIN_VALUE;
                for(ScoredConverterFactory sfactory : tails) {
                    // break when score not to the max and have a converter
                    if (! potentials.isEmpty() && currentScore != Integer.MIN_VALUE) {
                        if (currentScore > sfactory.score) {
                            return potentials;
                        }
                    } else {
                        currentScore = sfactory.score;
                    }

                    Type tailFactoryInType = sfactory.converterFactory.getFromType();
                    if (outType != tailFactoryInType) { // ignore when no progress
                        List<ScoredConverterFactory> headConverters  = findConverterFactories(inType, tailFactoryInType, params);
                        if (!headConverters.isEmpty()) {
                            List<ScoredConverterFactory> tailConverters = findConverterFactories(tailFactoryInType, outType, params);
                            if (!tailConverters.isEmpty()) {
                                for(ScoredConverterFactory hf : headConverters) {
                                    if (hf.converterFactory.newConverter(new ConvertingTypes(inType, tailFactoryInType), new DefaultContextFactoryBuilder(), params) != null) {
                                        for(ScoredConverterFactory tf : tailConverters) {
                                            if (hf.converterFactory.newConverter(new ConvertingTypes(tailFactoryInType, outType), new DefaultContextFactoryBuilder(), params) != null) {
                                                ComposedConverterFactory composedConverterFactory = new ComposedConverterFactory(hf.converterFactory, tf.converterFactory, tailFactoryInType);
                                                int score = composedConverterFactory.score(new ConvertingTypes(inType, outType)).getScore();
                                                potentials.add(new ScoredConverterFactory(score, composedConverterFactory));
                                                break;
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Collections.sort(potentials);
        return potentials;
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
    

    private static class ComposedConverterFactory<I, T, O> implements ConverterFactory<I, O> {

        private final ConverterFactory<I, T> headConverterFactory;
        private final ConverterFactory<T, O> tailConverterFactory;
        private final Type tType;

        private ComposedConverterFactory(ConverterFactory<I, T> headConverterFactory, ConverterFactory<T, O> tailConverterFactory, Type tType) {
            this.headConverterFactory = headConverterFactory;
            this.tailConverterFactory = tailConverterFactory;
            this.tType = tType;
        }

        @Override
        public Converter<? super I, ? extends O> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
            Converter<? super I, ? extends T> head = headConverterFactory.newConverter(new ConvertingTypes(targetedTypes.getFrom(), tType), contextFactoryBuilder, params);
            if (head == null) return null;
            Converter<? super T, ? extends O> tail = tailConverterFactory.newConverter(new ConvertingTypes(tType, targetedTypes.getTo()), contextFactoryBuilder);
            if (tail == null) return null;
            return new ComposedConverter<I, T, O>(head, tail);
        }

        @Override
        public ConvertingScore score(ConvertingTypes targetedTypes) {
            ConvertingScore headScore = headConverterFactory.score(new ConvertingTypes(targetedTypes.getFrom(), tType));
            ConvertingScore tailScore = tailConverterFactory.score(new ConvertingTypes(tType, targetedTypes.getTo()));
            return new ConvertingScore(Math.min(headScore.getFromScore(), tailScore.getFromScore()), Math.min(headScore.getToScore(), tailScore.getToScore()));
        }

        @Override
        public Type getFromType() {
            return headConverterFactory.getFromType();
        }
    }
    
}
