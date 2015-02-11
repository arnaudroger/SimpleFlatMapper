package org.sfm.map;


public interface ColumnDefinitionProvider<C extends ColumnDefinition<K, C>, K extends  FieldKey<K>> {
    public C getColumnDefinition(K key);
}
