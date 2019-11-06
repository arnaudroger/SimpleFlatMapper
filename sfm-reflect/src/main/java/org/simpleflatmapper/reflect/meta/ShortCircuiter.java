package org.simpleflatmapper.reflect.meta;

import java.util.Arrays;

public class ShortCircuiter {

    private final ClassMeta<?>[][] classMetas;
    private final PropertyNameMatcher[] propertyNameMatchers;

    public ShortCircuiter() {
        classMetas = new ClassMeta[0][];
        propertyNameMatchers = new PropertyNameMatcher[0];
    }

    private ShortCircuiter(ClassMeta<?>[][] classMetas, PropertyNameMatcher[] propertyNameMatchers) {
        this.classMetas = classMetas;
        this.propertyNameMatchers = propertyNameMatchers;
    }

    public ShortCircuiter eval(PropertyNameMatcher propertyNameMatcher, ClassMeta<?>... cm) {
        ClassMeta<?>[][] classMetas = Arrays.copyOf(this.classMetas, this.classMetas.length + 1);
        classMetas[classMetas.length - 1] = cm;
        PropertyNameMatcher[] propertyNameMatchers = Arrays.copyOf(this.propertyNameMatchers, this.propertyNameMatchers.length + 1);
        propertyNameMatchers[propertyNameMatchers.length - 1] = propertyNameMatcher;
        return new ShortCircuiter(classMetas, propertyNameMatchers);
    }

    public boolean shortCircuit() {
        if (propertyNameMatchers.length > 8) return true;

        if (propertyNameMatchers.length > 1) {
            PropertyNameMatcher lastMatch = propertyNameMatchers[propertyNameMatchers.length - 1];
            ClassMeta<?>[] lastMetas = classMetas[classMetas.length - 1];

            for(int i = 0; i < propertyNameMatchers.length -1; i++) {
                if (lastMatch == propertyNameMatchers[i] && Arrays.equals(lastMetas, classMetas[i])) {
                    return true;
                }
            }
        }

        return false;
    }
}
