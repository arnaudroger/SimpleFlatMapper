package org.sfm.jdbc.spring;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

public class SqlParameterSourceFactory<T> {
    private final PlaceHolderValueGetterSource<T> placeHolderValueGetterSource;

    public SqlParameterSourceFactory(PlaceHolderValueGetterSource<T> placeHolderValueGetterSource) {
        this.placeHolderValueGetterSource = placeHolderValueGetterSource;
    }

    public SqlParameterSource newSqlParameterSource(T value) {
        return new SqlParameterSourceImpl<T>(placeHolderValueGetterSource, value);
    }

    public SqlParameterSource[] newSqlParameterSources(Iterable<T> values) {
        return newSqlParameterSources(values.iterator());
    }

    public SqlParameterSource[] newSqlParameterSources(Iterator<T> values) {
        ArrayList<SqlParameterSource> sources = new ArrayList<SqlParameterSource>();
        while(values.hasNext()) {
            sources.add(newSqlParameterSource(values.next()));
        }
        return sources.toArray(new SqlParameterSource[sources.size()]);
    }

    public SqlParameterSource[] newSqlParameterSources(T[] values) {
        ArrayList<SqlParameterSource> sources = new ArrayList<SqlParameterSource>();
       for(T value : values) {
            sources.add(newSqlParameterSource(value));
        }
        return sources.toArray(new SqlParameterSource[sources.size()]);
    }

}
