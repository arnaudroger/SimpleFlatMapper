package org.springframework.jdbc.core.namedparam;

import java.util.List;

public final class ExposedParsedSql {

    public static ExposedParsedSql expose(ParsedSql parsedSql) {
        return new ExposedParsedSql(parsedSql);
    }

    private final ParsedSql delegate;

    private ExposedParsedSql(final ParsedSql delegate) {
        this.delegate = delegate;
    }

    public List<String> getParameterNames() {
        return delegate.getParameterNames();
    }

    public int[] getParameterIndexes(int parameterPosition) {
        return delegate.getParameterIndexes(parameterPosition);
    }

    public int getNamedParameterCount() {
        return delegate.getNamedParameterCount();
    }

    public int getUnnamedParameterCount() {
        return delegate.getUnnamedParameterCount();
    }

    public int getTotalParameterCount() {
        return delegate.getTotalParameterCount();
    }
}
