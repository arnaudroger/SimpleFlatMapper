package org.simpleflatmapper.reflect.instantiator;


import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.TypeAffinity;
import org.simpleflatmapper.util.TypeHelper;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class InstantiatorDefinitions {

    public static final Comparator<InstantiatorDefinition> COMPARATOR = new Comparator<InstantiatorDefinition>() {
        @Override
        public int compare(InstantiatorDefinition o1, InstantiatorDefinition o2) {
            InstantiatorDefinition.Type t1 = o1.getType();
            InstantiatorDefinition.Type t2 = o2.getType();

            int d = t1.ordinal() - t2.ordinal();

            if (d != 0) return d;

            if (isValueOf(o1)) {
                if (!isValueOf(o2)) {
                    return -1;
                }
            } else if (isValueOf(o2)) {
                return 1;
            }

            final int p = o1.getParameters().length - o2.getParameters().length;

            if (p == 0) {
                return o1.getName().compareTo(o2.getName());
            }
            return p;
        }
    };

    private static boolean isValueOf(InstantiatorDefinition d) {
        if (d.getType() != InstantiatorDefinition.Type.METHOD) return false;
        String name = d.getName();
        return name.equals("valueOf") || name.equals("of") || name.equals("newInstance");
    }


    public static InstantiatorDefinition lookForCompatibleOneArgument(List<InstantiatorDefinition> col, CompatibilityScorer scorer) {
        if (col == null) return null;
        InstantiatorDefinition current = null;
        int currentScore = -1;

        for(int i = 0; i < col.size(); i++) {
            InstantiatorDefinition id = col.get(i);
            if (id.getParameters().length == 1) {
                int score = scorer.score(id);
                if (score > currentScore) {
                    current = id;
                    currentScore = score;
                }
            }
        }
        return current;
    }

    public static InstantiatorDefinitions.CompatibilityScorer getCompatibilityScorer(Object key) {
        if (key instanceof TypeAffinity) {
            TypeAffinity ta = (TypeAffinity) key;
            Class<?>[] affinities = ta.getAffinities();

            if (affinities != null && affinities.length > 0) {
                return new TypeAffinityCompatibilityScorer(affinities);
            }
        }
        return new DefaultCompatibilityScorer();
    }

    private static class DefaultCompatibilityScorer implements InstantiatorDefinitions.CompatibilityScorer {
        @Override
        public int score(InstantiatorDefinition id) {
            Class<?> type = id.getParameters()[0].getType();
            Package aPackage = type.getPackage();
            if (type.isPrimitive() || (aPackage != null && aPackage.getName().equals("java.lang"))) {
                return 1;
            }
            return 0;
        }
    }

    private static class TypeAffinityCompatibilityScorer implements InstantiatorDefinitions.CompatibilityScorer {
        private final Class<?>[] classes;

        private TypeAffinityCompatibilityScorer(Class<?>[] classes) {
            this.classes = classes;
        }

        @Override
        public int score(InstantiatorDefinition id) {
            Class<?> paramType = TypeHelper.toBoxedClass(id.getParameters()[0].getType());

            for(int i = 0; i < classes.length; i++) {
                Class<?> c = classes[i];
                if (c.isAssignableFrom(paramType)) {
                    return classes.length - i + 10;
                }
            }

            Package aPackage = paramType.getPackage();
            if (aPackage != null && aPackage.getName().equals("java.lang")) {
                return 1;
            }
            return 0;
        }
    }

    public interface CompatibilityScorer {
        int score(InstantiatorDefinition id);
    }
}
