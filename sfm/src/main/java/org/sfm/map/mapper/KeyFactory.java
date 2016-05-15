package org.sfm.map.mapper;

import org.sfm.map.FieldKey;

public interface KeyFactory<K extends FieldKey<K>> {

    K newKey(String name, int i);

}
