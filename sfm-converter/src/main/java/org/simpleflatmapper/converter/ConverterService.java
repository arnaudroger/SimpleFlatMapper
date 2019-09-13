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
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

public class ConverterService {


    private static final ConverterService INSTANCE = new ConverterService(getConverterFactories(ConverterService.class.getClassLoader()));

    private static List<ContextualConverterFactory> getConverterFactories(ClassLoader classLoader) {
        final List<ContextualConverterFactory> converterFactories = new ArrayList<ContextualConverterFactory>();

        final Consumer<ContextualConverterFactory<?, ?>> factoryConsumer = new Consumer<ContextualConverterFactory<?, ?>>() {
            @Override
            public void accept(ContextualConverterFactory<?, ?> converterFactory) {
                converterFactories.add(converterFactory);
            }
        };

        new JavaBaseConverterFactoryProducer().produce(factoryConsumer);

        //IFJAVA8_START
        new JavaTimeConverterFactoryProducer().produce(factoryConsumer);
        //IFJAVA8_END

        ProducerServiceLoader.produceFromServiceLoader(ServiceLoader.load(ContextualConverterFactoryProducer.class, classLoader), factoryConsumer);
        ProducerServiceLoader.produceFromServiceLoader(ServiceLoader.load(ConverterFactoryProducer.class, classLoader), new Consumer<ConverterFactory<?, ?>>() {
            @Override
            public void accept(ConverterFactory<?, ?> converterFactory) {
                factoryConsumer.accept(new ContextualConverterFactoryAdapter(converterFactory));
            }
        });

        return converterFactories;
    }


    public static ConverterService getInstance() {
        return INSTANCE;
    }

    public static ConverterService getInstance(ClassLoader classLoader) {
        return new ConverterService(getConverterFactories(classLoader));
    }

    private ConverterService(List<ContextualConverterFactory> converters) {
        this.converters = converters;
    }

    public <F, P> Converter<? super F, ? extends P> findConverter(Class<F> inType, Class<P> outType, Object... params) {
        return findConverter((Type)inType, (Type)outType, params);
    }

    public <F, P> Converter<? super F, ? extends P> findConverter(Type inType, Type outType, Object... params) {

        DefaultContextFactoryBuilder builder = new DefaultContextFactoryBuilder();

        ContextualConverter<? super F, ? extends P> converter = findConverter((Type) inType, (Type) outType, builder, params);

        if (converter == null) return null;

        return new ConverterToContextualAdapter<F, P>(converter, builder.build());
    }

    public <F, P> ContextualConverter<? super F, ? extends P> findConverter(Class<F> inType, Class<P> outType, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
        return findConverter((Type)inType, (Type)outType, contextFactoryBuilder, params);
    }

    @SuppressWarnings("unchecked")
    public <F, P> ContextualConverter<? super F, ? extends P> findConverter(Type inType, Type outType, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
        if (TypeHelper.isAssignable(outType, inType)) {
            return new IdentityConverter();
        }

        List<ScoredConverterFactory> potentials = findConverterFactories(inType, outType, params);


        ConvertingTypes targetedTypes = new ConvertingTypes(inType, outType);
        for(ScoredConverterFactory p : potentials) {
            ContextualConverter<F, P> converter = p.converterFactory.newConverter(targetedTypes, contextFactoryBuilder, params);
            if (converter != null) return converter;
        }
        return null;

    }

    @SuppressWarnings("unchecked")
    public  List<ScoredConverterFactory> findConverterFactories(Type inType, Type outType, Object... params) {
        return findConverterFactories(inType, outType, params, new HashSet<Type>());
    }
    private  List<ScoredConverterFactory> findConverterFactories(Type inType, Type outType, Object[] params, Set<Type> loopDetector) {
        List<ScoredConverterFactory> potentials = new ArrayList<ScoredConverterFactory>();

        List<ScoredConverterFactory> tails = new ArrayList<ScoredConverterFactory>();

        ConvertingTypes targetedTypes = new ConvertingTypes(inType, outType);

        for(ContextualConverterFactory converterFactory : converters) {
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
                    
                    if (outType != tailFactoryInType && ! loopDetector.contains(tailFactoryInType)) { // ignore when no progress
                        Set<Type> subLoopDetectors = new HashSet<Type>(loopDetector);
                        subLoopDetectors.add(tailFactoryInType);
                        List<ScoredConverterFactory> headConverters  = findConverterFactories(inType, tailFactoryInType, params, subLoopDetectors);
                        if (!headConverters.isEmpty()) {
                            List<ScoredConverterFactory> tailConverters = findConverterFactories(tailFactoryInType, outType, params, subLoopDetectors);
                            if (!tailConverters.isEmpty()) {
                                for(ScoredConverterFactory hf : headConverters) {
                                    try {
                                        if (hf.converterFactory.newConverter(new ConvertingTypes(inType, tailFactoryInType), new DefaultContextFactoryBuilder(), params) != null) {
                                            for (ScoredConverterFactory tf : tailConverters) {
                                                if (hf.converterFactory.newConverter(new ConvertingTypes(tailFactoryInType, outType), new DefaultContextFactoryBuilder(), params) != null) {
                                                    ComposedConverterFactory composedConverterFactory = new ComposedConverterFactory(hf.converterFactory, tf.converterFactory, tailFactoryInType);
                                                    int score = composedConverterFactory.score(new ConvertingTypes(inType, outType)).getScore();
                                                    potentials.add(new ScoredConverterFactory(score, composedConverterFactory));
                                                    break;
                                                }
                                            }
                                            break;
                                        }
                                    } catch (IllegalStateException e) {
                                        // ignore no format
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

    private final List<ContextualConverterFactory> converters;


    private static class ScoredConverterFactory implements Comparable<ScoredConverterFactory>{
        private final int score;
        private final ContextualConverterFactory converterFactory;

        private ScoredConverterFactory(int score, ContextualConverterFactory converterFactory) {
            this.score = score;
            this.converterFactory = converterFactory;
        }

        @Override
        public int compareTo(ScoredConverterFactory o) {
            return o.score - score;
        }
    }
    

    private static class ComposedConverterFactory<I, T, O> implements ContextualConverterFactory<I, O> {

        private final ContextualConverterFactory<I, T> headConverterFactory;
        private final ContextualConverterFactory<T, O> tailConverterFactory;
        private final Type tType;

        private ComposedConverterFactory(ContextualConverterFactory<I, T> headConverterFactory, ContextualConverterFactory<T, O> tailConverterFactory, Type tType) {
            this.headConverterFactory = headConverterFactory;
            this.tailConverterFactory = tailConverterFactory;
            this.tType = tType;
        }

        @Override
        public ContextualConverter<? super I, ? extends O> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
            ContextualConverter<? super I, ? extends T> head = headConverterFactory.newConverter(new ConvertingTypes(targetedTypes.getFrom(), tType), contextFactoryBuilder, params);
            if (head == null) return null;
            ContextualConverter<? super T, ? extends O> tail = tailConverterFactory.newConverter(new ConvertingTypes(tType, targetedTypes.getTo()), contextFactoryBuilder);
            if (tail == null) return null;
            return new ComposedContextualConverter<I, T, O>(head, tail);
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
