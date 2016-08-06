package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.FieldKey;

public interface KeyFactory<K extends FieldKey<K>> {

    K newKey(String name, int i);

}
