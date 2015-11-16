package org.sfm.jdbc.spring;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

public final class StaticSqlParameters<T> implements SqlParameters<T> {

    private final PlaceHolder<T>[] parameters;

    public StaticSqlParameters(PlaceHolder<T>[] parameters) {
        this.parameters = parameters;
    }

    @Override
    public PlaceHolder<T> getParameter(String column) {
        for(PlaceHolder<T> parameter : parameters) {
            if (parameter.isColumn(column)) {
                return parameter;
            }
        }
        return null;
    }

    @Override
    public SqlParameterSource value(T value) {
        return new SqlParameterSourceImpl<T>(this, value);
    }

    public PlaceHolder<T>[] getParameters() {
        return parameters;
    }
}
