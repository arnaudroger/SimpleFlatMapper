package org.simpleflatmapper.jdbc.spring;


public interface PlaceHolderValueGetterSource<T> {
    PlaceHolderValueGetter<T> getPlaceHolderValueGetter(String column);

    Iterable<PlaceHolderValueGetter<T>> getParameters();
}
