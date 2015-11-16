package org.sfm.jdbc.spring;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

public final class SqlParameterSourceImpl<T> implements SqlParameterSource {
    private final SqlParameters<T> parameters;
    private final T instance;

    public SqlParameterSourceImpl(SqlParameters<T> parameters, T instance) {
        this.parameters = parameters;
        this.instance = instance;
    }

    @Override
    public boolean hasValue(String column) {
        return parameters.getParameter(column) != null;
    }

    @Override
    public Object getValue(String column) throws IllegalArgumentException {
        PlaceHolder<T> parameter = parameters.getParameter(column);
        if (parameter != null) {
            return parameter.getValue(instance);
        } else {
            throw new IllegalArgumentException("No value for column " + column);
        }
    }

    @Override
    public int getSqlType(String column) {
        PlaceHolder<T> parameter = parameters.getParameter(column);
        if (parameter != null) {
            return parameter.getSqlType();
        } else {
            return TYPE_UNKNOWN;
        }
    }

    @Override
    public String getTypeName(String column) {
        PlaceHolder<T> parameter = parameters.getParameter(column);
        if (parameter != null) {
            return parameter.getTypeName();
        } else {
            return null;
        }
    }
}
