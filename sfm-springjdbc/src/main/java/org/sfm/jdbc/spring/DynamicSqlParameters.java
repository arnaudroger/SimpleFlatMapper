package org.sfm.jdbc.spring;

import org.sfm.reflect.meta.ClassMeta;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.concurrent.atomic.AtomicReference;

public class DynamicSqlParameters<T> implements SqlParameters<T> {

    private final AtomicReference<StaticSqlParameters<T>> delegate;
    private final ClassMeta<T> classMeta;

    @SuppressWarnings("unchecked")
    public DynamicSqlParameters(ClassMeta<T> classMeta) {
        this.classMeta = classMeta;
        this.delegate = new AtomicReference<StaticSqlParameters<T>>(new StaticSqlParameters<T>(new PlaceHolder[0]));
    }

    @Override
    public PlaceHolder<T> getParameter(String column) {
        do {
            StaticSqlParameters<T> ssp = delegate.get();
            PlaceHolder<T> parameter = ssp.getParameter(column);
            if (parameter != null) {
                return parameter;
            } else {
                StaticSqlParameters<T> nssp = addColumn(ssp, column);
                if (delegate.compareAndSet(ssp, nssp)) {
                    return nssp.getParameter(column);
                }
            }
        } while(true);
    }

    private StaticSqlParameters<T> addColumn(StaticSqlParameters<T> ssp, String column) {
        SqlParameterSourceBuilder<T> builder = new SqlParameterSourceBuilder<T>(classMeta);
        for(PlaceHolder<T> ph : ssp.getParameters()) {
            builder.add(ph.getColumn());
        }
        builder.add(column);
        return builder.build();
    }

    @Override
    public SqlParameterSource value(T value) {
        return new SqlParameterSourceImpl<T>(this, value);
    }
}
