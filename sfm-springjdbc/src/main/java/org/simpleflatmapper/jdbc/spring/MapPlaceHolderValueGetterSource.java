package org.simpleflatmapper.jdbc.spring;

import java.util.HashMap;
import java.util.Map;

public final class MapPlaceHolderValueGetterSource<T> implements PlaceHolderValueGetterSource<T> {

    private final Map<String, PlaceHolderValueGetter<T>> parameters;

    public MapPlaceHolderValueGetterSource(PlaceHolderValueGetter<T>[] parameters) {
        this.parameters = new HashMap<String, PlaceHolderValueGetter<T>>();

        for(PlaceHolderValueGetter<T> getter : parameters) {
            this.parameters.put(getter.getColumn(), getter);
        }
    }

    @Override
    public PlaceHolderValueGetter<T> getPlaceHolderValueGetter(String column) {
        return parameters.get(column);
    }

    @Override
    public Iterable<PlaceHolderValueGetter<T>> getParameters() {
        return parameters.values();
    }
}
