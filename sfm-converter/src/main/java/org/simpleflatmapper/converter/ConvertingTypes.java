package org.simpleflatmapper.converter;

import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;

public class ConvertingTypes {
    private static final int MAX_SCORE = 256;
    private static final int NOT_COMPATIBLE = -1;
    private final Type from;
    private final Type to;

    public ConvertingTypes(Type from, Type to) {
        this.from = from;
        this.to = to;
    }

    public ConvertingScore score(ConvertingTypes targetedTypes) {
        // score on target
        int targetScore = getTargetScore(to, targetedTypes.getTo());

        // score on source
        int sourceScore = getSourceScore(from, targetedTypes.getFrom());

        return  new ConvertingScore(sourceScore, targetScore);
    }

    public static int getSourceScore(Type from, Type target) {
        if (!TypeHelper.isAssignable(from, target)) {
            return NOT_COMPATIBLE;
        }

        if (TypeHelper.areEquals(from, target)) {
            return MAX_SCORE;
        }

        return Math.max(MAX_SCORE - distanceToParent(target, from), 0);
    }

    public static int distanceToParent(Type target, Type parent) {
        int distance = 0;

        Class<?> currentClass = TypeHelper.toClass(target);

        while(currentClass != null) {

            if (isParentThere(currentClass, parent)) {
                break;
            }


            currentClass = currentClass.getSuperclass();
            distance ++;
        }
        return distance;
    }

    private static boolean isParentThere(Class<?> currentClass, Type parent) {
        if (TypeHelper.areEquals(parent, currentClass)) {
            return true;
        }

        for (Class<?> interfaceClass : currentClass.getInterfaces()) {
            if (TypeHelper.areEquals(parent, interfaceClass)) {
                return true;
            }
        }
        return false;
    }

    public static int getTargetScore(Type from, Type target) {
        if (!TypeHelper.isAssignable(target, from)) {
            return NOT_COMPATIBLE;
        }

        if (TypeHelper.areEquals(from, target)) {
            return MAX_SCORE;
        }
        return Math.max(MAX_SCORE - distanceToParent(from, target), 0);
    }

    public Type getFrom() {
        return from;
    }

    public Type getTo() {
        return to;
    }

}
