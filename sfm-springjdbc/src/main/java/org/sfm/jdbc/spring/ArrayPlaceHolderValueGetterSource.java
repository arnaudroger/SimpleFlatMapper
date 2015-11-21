package org.sfm.jdbc.spring;

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

    public PlaceHolderValueGetter<T>[] getParameters() {
        return parameters;
    }
}
