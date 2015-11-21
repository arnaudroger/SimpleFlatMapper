package org.sfm.jdbc.spring;

import org.sfm.reflect.meta.ClassMeta;

import java.util.concurrent.atomic.AtomicReference;

public class DynamicPlaceHolderValueGetterSource<T> implements PlaceHolderValueGetterSource<T> {

    private final AtomicReference<ArrayPlaceHolderValueGetterSource<T>> delegate;
    private final ClassMeta<T> classMeta;

    @SuppressWarnings("unchecked")
    public DynamicPlaceHolderValueGetterSource(ClassMeta<T> classMeta) {
        this.classMeta = classMeta;
        this.delegate = new AtomicReference<ArrayPlaceHolderValueGetterSource<T>>(new ArrayPlaceHolderValueGetterSource<T>(new PlaceHolderValueGetter[0]));
    }

    @Override
    public PlaceHolderValueGetter<T> getPlaceHolderValueGetter(String column) {
        do {
            ArrayPlaceHolderValueGetterSource<T> ssp = delegate.get();
            PlaceHolderValueGetter<T> parameter = ssp.getPlaceHolderValueGetter(column);
            if (parameter != null) {
                return parameter;
            } else {
                ArrayPlaceHolderValueGetterSource<T> nssp = addColumn(ssp, column);
                if (delegate.compareAndSet(ssp, nssp)) {
                    return nssp.getPlaceHolderValueGetter(column);
                }
            }
        } while(true);
    }

    private ArrayPlaceHolderValueGetterSource<T> addColumn(ArrayPlaceHolderValueGetterSource<T> ssp, String column) {
        SqlParameterSourceBuilder<T> builder = new SqlParameterSourceBuilder<T>(classMeta);
        for(PlaceHolderValueGetter<T> ph : ssp.getParameters()) {
            builder.add(ph.getColumn());
        }
        builder.add(column);
        return builder.buildSource();
    }
}
