package org.simpleflatmapper.jdbc.spring;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

public final class SqlParameterSourceImpl<T> implements SqlParameterSource {
    private final PlaceHolderValueGetterSource<T> parameters;
    private final T instance;

    public SqlParameterSourceImpl(PlaceHolderValueGetterSource<T> parameters, T instance) {
        this.parameters = parameters;
        this.instance = instance;
    }

    @Override
    public boolean hasValue(String column) {
        PlaceHolderValueGetter<T> placeHolderValueGetter = parameters.getPlaceHolderValueGetter(column);
        return placeHolderValueGetter != null && placeHolderValueGetter.hasGetter();
    }

    @Override
    public Object getValue(String column) throws IllegalArgumentException {
        PlaceHolderValueGetter<T> parameter = parameters.getPlaceHolderValueGetter(column);
        if (parameter != null) {
            return parameter.getValue(instance );
        } else {
            throw new IllegalArgumentException("No value for property " + column);
        }
    }

    @Override
    public int getSqlType(String column) {
        PlaceHolderValueGetter<T> parameter = parameters.getPlaceHolderValueGetter(column);
        if (parameter != null) {
            return parameter.getSqlType();
        } else {
            return TYPE_UNKNOWN;
        }
    }

    @Override
    public String getTypeName(String column) {
        PlaceHolderValueGetter<T> parameter = parameters.getPlaceHolderValueGetter(column);
        if (parameter != null) {
            return parameter.getTypeName();
        } else {
            return null;
        }
    }
}
