package org.simpleflatmapper.core.map.mapper;

import org.simpleflatmapper.core.map.FieldKey;

public interface KeyFactory<K extends FieldKey<K>> {

    K newKey(String name, int i);

}
