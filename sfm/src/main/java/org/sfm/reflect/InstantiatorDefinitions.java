package org.sfm.reflect;


import java.util.Collection;
import java.util.Comparator;

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


    public static InstantiatorDefinition lookForCompatibleOneArgument(Collection<InstantiatorDefinition> col, CompatibilityScorer scorer) {
        InstantiatorDefinition current = null;
        int currentScore = -1;

        for(InstantiatorDefinition id : col ) {
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

    public interface CompatibilityScorer {
        int score(InstantiatorDefinition id);
    }
}
