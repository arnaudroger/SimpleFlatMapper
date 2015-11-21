package org.sfm.jdbc.spring;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

public class SqlParameterSourceFactory<T> {
    private final PlaceHolderValueGetterSource<T> placeHolderValueGetterSource;

    public SqlParameterSourceFactory(PlaceHolderValueGetterSource<T> placeHolderValueGetterSource) {
        this.placeHolderValueGetterSource = placeHolderValueGetterSource;
    }

    public SqlParameterSource newSqlParameterSource(T value) {
        return new SqlParameterSourceImpl<T>(placeHolderValueGetterSource, value);
    }
}
