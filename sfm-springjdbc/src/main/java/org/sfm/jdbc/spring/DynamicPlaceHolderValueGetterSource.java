package org.sfm.jdbc.spring;

import org.sfm.reflect.meta.ClassMeta;

import java.util.concurrent.atomic.AtomicReference;

public class DynamicPlaceHolderValueGetterSource<T> implements PlaceHolderValueGetterSource<T> {

    private final AtomicReference<PlaceHolderValueGetterSource<T>> delegate;
    private final ClassMeta<T> classMeta;

    @SuppressWarnings("unchecked")
    public DynamicPlaceHolderValueGetterSource(ClassMeta<T> classMeta) {
        this.classMeta = classMeta;
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
        SqlParameterSourceBuilder<T> builder = new SqlParameterSourceBuilder<T>(classMeta);
        for(PlaceHolderValueGetter<T> ph : ssp.getParameters()) {
            builder.add(ph.getColumn());
        }
        builder.add(column);
        return builder.buildSource();
    }
}
