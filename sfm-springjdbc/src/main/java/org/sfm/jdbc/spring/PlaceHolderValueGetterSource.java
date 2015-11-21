package org.sfm.jdbc.spring;


public interface PlaceHolderValueGetterSource<T> {
    PlaceHolderValueGetter<T> getPlaceHolderValueGetter(String column);
}
