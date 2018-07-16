package org.simpleflatmapper.reflect.instantiator;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.reflect.getter.ConstantIntGetter;
import org.simpleflatmapper.util.BiFunction;

import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.Map;

public class KotlinDefaultConstructorInstantiatorDefinition implements InstantiatorDefinition {
    
    private final ExecutableInstantiatorDefinition original;
    private final ExecutableInstantiatorDefinition defaulted;


    public KotlinDefaultConstructorInstantiatorDefinition(ExecutableInstantiatorDefinition original, ExecutableInstantiatorDefinition defaulted) {
        this.original = original;
        this.defaulted = defaulted;
    }

    public Member getExecutable() {
        return original.getExecutable();
    }

    @Override
    public Parameter[] getParameters() {
        return original.getParameters();
    }

    @Override
    public boolean hasParam(Parameter param) {
        return original.hasParam(param);
    }

    @Override
    public Type getType() {
        return original.getType();
    }

    @Override
    public String getName() {
        return original.getName();
    }

    @Override
    public String toString() {
        return original.toString();
    }

    public InstantiatorDefinition getDefaultValueConstructor() {
        Parameter[] defaultedParameters = defaulted.getParameters();
        Parameter[] originalParameters = original.getParameters();
        Parameter[] mergedParam = Arrays.copyOf(defaultedParameters, defaultedParameters.length);
        
        System.arraycopy(originalParameters, 0, mergedParam, 0, originalParameters.length);
        return new ExecutableInstantiatorDefinition(defaulted.getExecutable(), mergedParam);
    }

    public <S1, S2> void addDefaultValueFlagBi(Map<Parameter, BiFunction<? super S1, ? super S2, ?>> injections) {
        int nbParams = original.getParameters().length;
        int nbDefaulting = defaulted.getParameters().length - nbParams - 1;
        for(int i = 0; i < nbDefaulting; i++) {
            int startingParam = i  * Integer.SIZE;
            int endParam = Math.min(startingParam + Integer.SIZE, nbParams);
            int flag = 0;
            for(int j = startingParam; j <  endParam; j++) {
                if (!injections.containsKey(original.getParameters()[j])) {
                    flag |= 1 << (j - startingParam);
                }
            }
            
            final Integer ff = flag;
            injections.put(defaulted.getParameters()[nbParams + i], new BiFunction<Object, Object, Integer>() {
                @Override
                public Integer apply(Object o, Object o2) {
                    return ff;
                }
            });
        }
    }

    public <S> void addDefaultValueFlag(Map<Parameter, Getter<? super S, ?>> injections) {
        int nbParams = original.getParameters().length;
        int nbDefaulting = defaulted.getParameters().length - nbParams - 1;
        for(int i = 0; i < nbDefaulting; i++) {
            int startingParam = i  * Integer.SIZE;
            int endParam = Math.min(startingParam + Integer.SIZE, nbParams);
            int flag = 0;
            for(int j = startingParam; j <  endParam; j++) {
                if (!injections.containsKey(original.getParameters()[j])) {
                    flag |= 1 << (j - startingParam);
                }
            }
            injections.put(defaulted.getParameters()[nbParams + i], new ConstantIntGetter<Object>(flag));
        }
    }
    
}
