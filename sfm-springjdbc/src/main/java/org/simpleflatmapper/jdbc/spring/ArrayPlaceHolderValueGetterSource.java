package org.simpleflatmapper.jdbc.spring;

import java.util.Arrays;

public final class ArrayPlaceHolderValueGetterSource<T> implements PlaceHolderValueGetterSource<T> {

    private final PlaceHolderValueGetter<T>[] parameters;

    public ArrayPlaceHolderValueGetterSource(PlaceHolderValueGetter<T>[] parameters) {
        this.parameters = parameters;
    }

    @Override
    public PlaceHolderValueGetter<T> getPlaceHolderValueGetter(String column) {
        for(PlaceHolderValueGetter<T> parameter : parameters) {
            if (parameter.isColumn(column)) {
                return parameter;
            }
        }
        return null;
    }

    @Override
    public Iterable<PlaceHolderValueGetter<T>> getParameters() {
        return Arrays.asList(parameters);
    }
}
