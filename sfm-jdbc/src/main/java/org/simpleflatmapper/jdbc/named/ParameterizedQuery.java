package org.simpleflatmapper.jdbc.named;

public interface ParameterizedQuery {
    int getParametersSize();
    NamedParameter getParameter(int i);
}
