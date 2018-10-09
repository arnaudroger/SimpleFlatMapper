package org.simpleflatmapper.jdbc.spring;

import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.reflect.meta.ClassMeta;

import java.util.concurrent.atomic.AtomicReference;

public class DynamicPlaceHolderValueGetterSource<T> implements PlaceHolderValueGetterSource<T> {

    private final AtomicReference<PlaceHolderValueGetterSource<T>> delegate;
    private final ClassMeta<T> classMeta;
    private final MapperConfig<JdbcColumnKey, ?> mapperConfig;

    @SuppressWarnings("unchecked")
    public DynamicPlaceHolderValueGetterSource(ClassMeta<T> classMeta, MapperConfig<JdbcColumnKey, ?> mapperConfig) {
        this.classMeta = classMeta;
        this.mapperConfig = mapperConfig;
        this.delegate = new AtomicReference<PlaceHolderValueGetterSource<T>>(new ArrayPlaceHolderValueGetterSource<T>(new PlaceHolderValueGetter[0]));
    }

    @Override
    public PlaceHolderValueGetter<T> getPlaceHolderValueGetter(String column) {
        do {
            PlaceHolderValueGetterSource<T> ssp = delegate.get();
            PlaceHolderValueGetter<T> parameter = ssp.getPlaceHolderValueGetter(column);
            if (parameter != null) {
                return parameter;
            } else {
                PlaceHolderValueGetterSource<T> nssp = addColumn(ssp, column);
                if (delegate.compareAndSet(ssp, nssp)) {
                    return nssp.getPlaceHolderValueGetter(column);
                }
            }
        } while(true);
    }

    @Override
    public Iterable<PlaceHolderValueGetter<T>> getParameters() {
        return delegate.get().getParameters();
    }

    private PlaceHolderValueGetterSource<T> addColumn(PlaceHolderValueGetterSource<T> ssp, String column) {
        SqlParameterSourceBuilder<T> builder = new SqlParameterSourceBuilder<T>(classMeta, mapperConfig);
        for(PlaceHolderValueGetter<T> ph : ssp.getParameters()) {
            builder.add(ph.getColumn());
        }
        builder.add(column);
        return builder.buildSource();
    }
}
