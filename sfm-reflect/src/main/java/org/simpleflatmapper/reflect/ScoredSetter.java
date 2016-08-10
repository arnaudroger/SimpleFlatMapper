package org.simpleflatmapper.reflect;


import org.simpleflatmapper.reflect.setter.NullSetter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ScoredSetter<T, P>  {

    private final int score;
    private final Setter<? super T, ? super P> setter;

    public ScoredSetter(int score, Setter<? super T, ? super P> setter) {
        this.score = score;
        this.setter = setter;
    }

    public Setter<? super T, ? super P> getSetter() {
        return setter;
    }

    @Override
    public String toString() {
        return "ScoredSetter{" +
                "score=" + score +
                ", setter=" + setter +
                '}';
    }

    public boolean isBetterThan(ScoredSetter<T, P> scoredSetter) {
        return score > scoredSetter.score;
    }

    public ScoredSetter<T, P> best(ScoredSetter<T, P> setter) {
        return isBetterThan(setter) ? this : setter;
    }

    public static <T, P> ScoredSetter<T, P> nullSetter() {
        return new ScoredSetter<T, P>(Integer.MIN_VALUE, NullSetter.NULL_SETTER);
    }

    public static <T, P> ScoredSetter<T, P> ofMethod(Method method, Setter<T, P> methodSetter) {
        int score = 2;
        if (method.getName().startsWith("set")) {
            score *= 2;
        }
        return of(methodSetter, score);
    }

    public static <T, P> ScoredSetter<T, P> ofField(Field field, Setter<T, P> fieldSetter) {
        return of(fieldSetter, 1);
    }

    public static <T, P> ScoredSetter<T, P> of(Setter<T, P> methodSetter, int score) {
        return new ScoredSetter<T, P>(score, methodSetter);
    }
}
