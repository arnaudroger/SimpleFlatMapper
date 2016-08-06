package org.simpleflatmapper.reflect;

public interface InstantiatorDefinition {
    Parameter[] getParameters();

    boolean hasParam(Parameter param);

    Type getType();

    String getName();


    enum Type {
        CONSTRUCTOR, METHOD, BUILDER;
    }
}
