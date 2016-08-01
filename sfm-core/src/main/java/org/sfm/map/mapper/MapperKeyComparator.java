package org.sfm.map.mapper;

import org.sfm.map.FieldKey;

import java.util.Comparator;

public abstract class MapperKeyComparator<K extends FieldKey<K>>  implements Comparator<MapperKey<K>> {
}
